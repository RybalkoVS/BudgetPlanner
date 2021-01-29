package by.grsu.budgetplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import by.grsu.budgetplanner.fragments.HomeFragment;
import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.fragments.StatisticsFragment;
import by.grsu.budgetplanner.fragments.TransactionsListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        pager.setOffscreenPageLimit(3);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            int currentPosition = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FragmentLifeCycle fragmentToShow = (FragmentLifeCycle)pagerAdapter.getItem(position);
                fragmentToShow.onResumeFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new TransactionsListFragment();
                case 2:
                    return new StatisticsFragment();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.tab_home);
                case 1:
                    return getResources().getText(R.string.tab_transactions_list);
                case 2:
                    return getResources().getText(R.string.tab_statistics);
            }
            return null;
        }
    }
    public interface FragmentLifeCycle
    {
        public void onResumeFragment();
    }
}