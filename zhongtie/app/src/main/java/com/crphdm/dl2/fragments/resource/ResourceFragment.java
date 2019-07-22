package com.crphdm.dl2.fragments.resource;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crphdm.dl2.R;
import com.crphdm.dl2.utils.Constant;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * 资源页
 */
public class ResourceFragment extends Fragment {
    //资源
    @Bind(R.id.tb_institution_library)
    TabLayout tbInstitutionLibrary;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private static final String TAG = ResourceFragment.class.getSimpleName();

    private int mNetState;
    //初始化Fragment。可通过参数savedInstanceState获取之前保存的值。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
        }

        if(mNetState == Constant.NET_STATE_FIRST_LEVEL || mNetState == Constant.NET_STATE_ALL){

        }else {
            //显示无网
//            Toast.makeText(getActivity(),"网络异常,无法访问北控中心!",Toast.LENGTH_SHORT).show();
        }
    }

    public static ResourceFragment newInstance(int netState) {
        ResourceFragment fragment = new ResourceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        fragment.setArguments(bundle);
        return fragment;
    }
    //通过参数savedInstanceState获取之前保存的值
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
    //初始化Fragment的布局。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
    //执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("全部"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("热门"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("最新"));

//        showAllResourceFragment();
        showSelfResourceFragment();

        tbInstitutionLibrary.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                if (tab.getPosition() == 0) {
//                    showAllResourceFragment();
//                    Ln.d("ResourceFragment:全部");
//                } else
                if (tab.getPosition() == 0) {
                    showSelfResourceFragment();
                    Ln.d("ResourceFragment:热门");
                } else if (tab.getPosition() == 1) {
                    showShareResourceFragment();
                    Ln.d("ResourceFragment:最新");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

//    private ResourceListFragment mAllResourceListFragment;
    private ResourceListFragment mHotResourceListFragment;
    private ResourceListFragment mNewResourceListFragment;

//    /**
//     * 显示全部资源
//     */
//    private void showAllResourceFragment() {
//        if (mAllResourceListFragment == null) {
//            mAllResourceListFragment = ResourceListFragment.newInstance(ResourceListFragment.TYPE_ALL);
//        }
//
//        getChildFragmentManager().beginTransaction().replace(
//                R.id.frame_layout, mAllResourceListFragment).commit();
//    }

    /**
     * 显示热门资源
     */
    private void showSelfResourceFragment() {
        if (mHotResourceListFragment == null) {
            mHotResourceListFragment = ResourceListFragment.newInstance(ResourceListFragment.TYPE_HOT);
        }

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, mHotResourceListFragment).commit();
    }

    /**
     * 显示最新资源
     */
    private void showShareResourceFragment() {
        if (mNewResourceListFragment == null) {
            mNewResourceListFragment = ResourceListFragment.newInstance(ResourceListFragment.TYPE_NEW);
        }

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, mNewResourceListFragment).commit();
    }
    //执行该方法时，Fragment处于活动状态，用户可与之交互。
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("资源页");
    }
    //执行该方法时，Fragment处于暂停状态，但依然可见，用户不能与之交互。
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("资源页");
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this,null);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }catch (IllegalAccessException e){
//            e.printStackTrace();
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this , null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
