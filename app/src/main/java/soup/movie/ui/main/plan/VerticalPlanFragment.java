package soup.movie.ui.main.plan;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import soup.movie.R;
import soup.movie.data.model.Movie;
import soup.movie.di.scope.FragmentScoped;
import soup.movie.ui.main.MainTabFragment;
import soup.movie.ui.main.plan.PlanViewState.DoneState;
import soup.movie.ui.main.plan.PlanViewState.LoadingState;
import timber.log.Timber;

import static soup.movie.util.RecyclerViewUtil.createGridLayoutManager;

@FragmentScoped
public class VerticalPlanFragment extends MainTabFragment implements PlanContract.View {

    @Inject
    PlanContract.Presenter presenter;

    private VerticalPlanListAdapter adapterView;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.list)
    RecyclerView listView;

    public VerticalPlanFragment() {
    }

    public static VerticalPlanFragment newInstance() {
        return new VerticalPlanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vertical_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();

        adapterView = new VerticalPlanListAdapter(getActivity());
        RecyclerView recyclerView = listView;
        recyclerView.setLayoutManager(createGridLayoutManager(context, 3));
        recyclerView.setAdapter(adapterView);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.getItemAnimator().setAddDuration(200);
        recyclerView.getItemAnimator().setRemoveDuration(200);

        swipeRefreshLayout.setOnRefreshListener(() -> presenter.refresh());

        presenter.attach(this);
    }

    @Override
    public void onDestroyView() {
        presenter.detach();
        super.onDestroyView();
    }

    @Override
    public void render(@NonNull PlanViewState viewState) {
        Timber.i("render: %s", viewState);
        if (viewState instanceof LoadingState) {
            renderInternal((LoadingState) viewState);
        } else if (viewState instanceof DoneState) {
            renderInternal((DoneState)viewState);
        } else {
            throw new IllegalStateException("Unknown UI Model");
        }
    }

    private void renderInternal(@NonNull LoadingState viewState) {
        swipeRefreshLayout.setRefreshing(true);
        updateMovieList(null);
    }

    private void renderInternal(@NonNull DoneState viewState) {
        swipeRefreshLayout.setRefreshing(false);
        updateMovieList(viewState.getMovies());
    }

    private void updateMovieList(@Nullable List<Movie> movieList) {
        VerticalPlanListAdapter adapterView = this.adapterView;
        if (adapterView != null) {
            adapterView.updateList(movieList);
        }
    }
}
