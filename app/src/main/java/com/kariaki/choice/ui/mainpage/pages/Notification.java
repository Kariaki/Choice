package com.kariaki.choice.ui.MainPage.Pages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kariaki.choice.R;
import com.kariaki.choice.ui.MainPage.Pages.Notifications.Adapters.PageAdapter;
import com.kariaki.choice.ui.MainPage.Pages.Notifications.GeneralNotification;
import com.kariaki.choice.ui.MainPage.Pages.Notifications.MyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment {

    public Notification() {
        // Required empty public constructor
    }

    public interface onClickListener {
        void clickListener();
    }

    public MyContent.onClickListener listener;

    public void setListener(MyContent.onClickListener listener) {
        this.listener = listener;
    }


    TabLayout notifactionTabLayout;
    ViewPager2 pager;
    PageAdapter adapter;
    AppBarLayout notifactionToolbar;
    List<Fragment> pages = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.notification, container, false);
        notifactionTabLayout = view.findViewById(R.id.notifactionTabLayout);
        pager = view.findViewById(R.id.notificationViewPager);
        notifactionToolbar = view.findViewById(R.id.notifactionToolbar);

        assert getFragmentManager() != null;
        adapter = new PageAdapter(getActivity());

        MyContent content = new MyContent();
        content.setListener(listener);

        pages.add(content);
        pages.add(new GeneralNotification());

        adapter.setPages(pages);

        pager.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(notifactionTabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("My Contents");
                        break;
                    case 1:
                        tab.setText("Notification");
                        break;
                }
            }
        });
        mediator.attach();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
