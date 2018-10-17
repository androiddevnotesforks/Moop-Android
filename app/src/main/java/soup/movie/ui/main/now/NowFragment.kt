package soup.movie.ui.main.now

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.fragment_vertical_list.*
import soup.movie.R
import soup.movie.data.MovieSelectManager
import soup.movie.databinding.FragmentVerticalListBinding
import soup.movie.theme.util.getColorAttr
import soup.movie.ui.detail.DetailActivity
import soup.movie.ui.helper.EventAnalytics
import soup.movie.ui.main.BaseTabFragment
import soup.movie.ui.main.now.NowViewState.*
import soup.movie.util.log.printRenderLog
import soup.movie.util.setVisibleIf
import javax.inject.Inject

class NowFragment :
        BaseTabFragment<NowContract.View, NowContract.Presenter>(),
        NowContract.View, BaseTabFragment.OnReselectListener {

    @Inject
    override lateinit var presenter: NowContract.Presenter

    @Inject
    lateinit var analytics: EventAnalytics

    private val listAdapter by lazy {
        NowListAdapter { index, movie, sharedElements ->
            analytics.clickItem(index, movie)
            MovieSelectManager.select(movie)
            val intent = Intent(requireActivity(), DetailActivity::class.java)
            startActivityForResult(intent, 0, ActivityOptions
                    .makeSceneTransitionAnimation(requireActivity(), *sharedElements)
                    .toBundle())
        }
    }

    override fun onMapSharedElements(names: List<String>,
                                     sharedElements: MutableMap<String, View>) {
        sharedElements.clear()
        MovieSelectManager.getSelectedItem()?.run {
            listView.findViewWithTag<View>(id)?.let { movieView ->
                names.forEach { name ->
                    val id: Int = when (name) {
                        "background" -> R.id.backgroundView
                        "poster" -> R.id.posterView
                        "age_bg" -> R.id.ageBgView
                        "new" -> R.id.newView
                        "best" -> R.id.bestView
                        "d_day" -> R.id.dDayView
                        else -> View.NO_ID
                    }
                    movieView.findViewById<View>(id)?.let {
                        sharedElements[name] = it
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            FragmentVerticalListBinding.inflate(inflater, container, false).root

    override fun initViewState(ctx: Context) {
        super.initViewState(ctx)
        listView.apply {
            adapter = listAdapter
            itemAnimator = SlideInUpAnimator().apply {
                addDuration = 200
                removeDuration = 200
            }
        }
        swipeRefreshLayout.apply {
            setProgressBackgroundColorSchemeColor(ctx.getColorAttr(R.attr.moop_stageColor))
            setColorSchemeColors(ctx.getColorAttr(R.attr.moop_stageLightColor))
            setOnRefreshListener {
                presenter.refresh()
            }
        }
        errorView.setOnClickListener {
            presenter.refresh()
        }
    }

    override fun render(viewState: NowViewState) {
        printRenderLog { viewState }
        swipeRefreshLayout?.isRefreshing = viewState is LoadingState
        errorView?.setVisibleIf { viewState is ErrorState }
        if (viewState is DoneState) {
            listAdapter.submitList(viewState.movies)
        }
    }

    override fun onReselect() {
        listView?.smoothScrollToPosition(0)
    }

    companion object {

        fun newInstance(): NowFragment = NowFragment()
    }
}
