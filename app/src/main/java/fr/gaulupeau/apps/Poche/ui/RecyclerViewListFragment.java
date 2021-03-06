package fr.gaulupeau.apps.Poche.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import fr.gaulupeau.apps.InThePoche.R;

public abstract class RecyclerViewListFragment<T, V extends RecyclerView.Adapter<?>>
        extends Fragment implements Sortable, Searchable {

    private static final String TAG = "RecyclerVLFragment";

    protected static final String STATE_SORT_ORDER = "sort_order";
    protected static final String STATE_SEARCH_QUERY = "search_query";

    protected Sortable.SortOrder sortOrder;
    protected String searchQuery;

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerViewLayoutManager;

    protected List<T> itemList;
    protected V listAdapter;
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected boolean active = false; // TODO: check: doesn't work as expected in PagerAdapter
    protected boolean invalidList = true;

    public RecyclerViewListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "onCreate()");

        if (savedInstanceState != null) {
            Log.v(TAG, "onCreate() restoring state");

            if (sortOrder == null) {
                sortOrder = Sortable.SortOrder.values()[savedInstanceState.getInt(STATE_SORT_ORDER)];
            }
            if (searchQuery == null) {
                searchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY);
            }
        }
        if (sortOrder == null) sortOrder = Sortable.SortOrder.DESC;

        itemList = new ArrayList<>();

        listAdapter = createListAdapter(itemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResID(), container, false);

        recyclerView = view.findViewById(getRecyclerViewResID());

        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(recyclerViewLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(page, totalItemsCount);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(listAdapter);

        refreshLayout = view.findViewById(getSwipeContainerResID());
        refreshLayout.setOnRefreshListener(this::onSwipeRefresh);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        active = true;

        checkList();
    }

    @Override
    public void onPause() {
        super.onPause();

        active = false;

        if (refreshLayout != null) {
            // http://stackoverflow.com/a/27073879
            refreshLayout.setRefreshing(false);
            refreshLayout.clearAnimation();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "onSaveInstanceState()");

        if (sortOrder != null) outState.putInt(STATE_SORT_ORDER, sortOrder.ordinal());
        if (searchQuery != null) outState.putString(STATE_SEARCH_QUERY, searchQuery);
    }

    @Override
    public void setSortOrder(Sortable.SortOrder sortOrder) {
        Sortable.SortOrder oldSortOrder = this.sortOrder;
        this.sortOrder = sortOrder;

        if (sortOrder != oldSortOrder) invalidateList();
    }

    @Override
    public void setSearchQuery(String searchQuery) {
        String oldSearchQuery = this.searchQuery;
        this.searchQuery = searchQuery;

        if (!TextUtils.equals(oldSearchQuery, searchQuery)) invalidateList();
    }

    public void invalidateList() {
        invalidList = true;

        if (active) checkList();
    }

    protected void checkList() {
        if (invalidList) {
            invalidList = false;

            resetContent();
        }
    }

    protected @LayoutRes int getLayoutResID() {
        return R.layout.list;
    }

    protected @IdRes int getRecyclerViewResID() {
        return R.id.list_recyclerView;
    }

    protected @IdRes int getSwipeContainerResID() {
        return R.id.list_swipeContainer;
    }

    protected abstract V createListAdapter(List<T> list);

    protected void resetContent() {
        boolean scrollToTop = false;
        int currentPage = -1;
        int perPage = getItemCountPerPage();
        if (recyclerViewLayoutManager != null) {
            int scrollPosition = recyclerViewLayoutManager.findFirstVisibleItemPosition();
            scrollToTop = scrollPosition == 0;
            currentPage = scrollPosition / perPage;
        }

        int preserveUntilPage;
        if (currentPage == -1 || currentPage == 0) {
            preserveUntilPage = 0;
        } else {
            List<T> lastPage = getItems(currentPage - 1);
            preserveUntilPage =
                    getDiffUtilCallback(itemList, lastPage).areItemsTheSame(currentPage * perPage - 1, perPage - 1)
                    ? currentPage : 0;
        }

        List<T> items = getItems(preserveUntilPage);
        List<T> nextPageItems = getItems(preserveUntilPage + 1);

        List<T> oldItemList = new ArrayList<T>(itemList);

        itemList.subList(preserveUntilPage * perPage, itemList.size()).clear();
        itemList.addAll(items);
        itemList.addAll(nextPageItems);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(getDiffUtilCallback(oldItemList, itemList));

        diffResult.dispatchUpdatesTo(listAdapter);

        if (scrollListener != null) scrollListener.resetState(preserveUntilPage + 1 /* Because we loaded an extra page */);

        if (scrollToTop && recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    protected void loadMore(int page, final int totalItemsCount) {
        Log.d(TAG, String.format("loadMore(page: %d, totalItemsCount: %d)", page, totalItemsCount));

        List<T> items = getItems(page);
        final int addedItemsCount = items.size();

        itemList.addAll(items);

        recyclerView.post(() -> listAdapter.notifyItemRangeInserted(totalItemsCount, addedItemsCount));
    }

    protected abstract List<T> getItems(int page);

    protected abstract int getItemCountPerPage();

    protected abstract DiffUtil.Callback getDiffUtilCallback(List<T> oldItems, List<T> newItems);

    protected void onSwipeRefresh() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

}
