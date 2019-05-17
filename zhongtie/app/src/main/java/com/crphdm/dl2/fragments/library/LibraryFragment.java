package com.crphdm.dl2.fragments.library;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.MoreFragmentActivity;
import com.crphdm.dl2.activity.library.UploadSelfResourceActivity;
import com.crphdm.dl2.qrcode.MipcaActivityCapture;
import com.crphdm.dl2.user.UserModule;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibraryFragment extends Fragment {
    //图书馆首页
    @Bind(R.id.ll_more_library)
    LinearLayout llMoreLibrary;
    @Bind(R.id.ll_scan_code)
    LinearLayout llScanCode;
    @Bind(R.id.ll_upload_data)
    LinearLayout llUploadData;
    @Bind(R.id.tb_institution_library)
    TabLayout tbInstitutionLibrary;
    @Bind(R.id.frame_layout)
    FrameLayout frameLayout;

    @Bind(R.id.ll_select_data)
    LinearLayout llSelectData;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private final static int SCANNING_QR_CODE = 1;
    private LibraryElectronicBookFragment mLibraryElectronicBookFragment;

    private int mNetState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
            Ln.d("LibraryFragment:mNetState:" + mNetState);
        }
    }

    public static LibraryFragment newInstance(int netState) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
        llSelectData.setVisibility(View.GONE);
//        }else {
//            llSelectData.setVisibility(View.GONE);
//        }
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("电子书"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("自有资源"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("共享资源"));



        showElectronicBookFragment();

        tbInstitutionLibrary.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    showElectronicBookFragment();
                } else if (tab.getPosition() == 1) {
                    showSelfResourceFragment();
                } else if (tab.getPosition() == 2) {
                    showShareResourceFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /**
         * 更多图书馆
         */
        llMoreLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showInstitutionLibraryListFragment();
//                mListener.onClickInstitutionLibraryList();
                startActivity(new Intent(getActivity(), MoreFragmentActivity.class));
            }
        });

        /**
         * 上传资料
         */
        llUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadSelfResourceActivity.class));
            }
        });

    }

    /**
     * 扫描二维码
     */
    @OnClick(R.id.ll_scan_code)
    public void scanCode() {
        if(Build.VERSION.SDK_INT>=23){
            addpermission();
        }else {
            if (UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND) != null &&
                    UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getUserid() != 0) {

                Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivityForResult(intent, SCANNING_QR_CODE);
            } else {
                showDialog("您不是本机构用户，请联系管理员");

            }
        }

    }
    public void addpermission(){
        if(!(getActivity().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)){
            requestCameraPermission();
        }else {
            if (UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND) != null &&
                    UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getUserid() != 0) {

                Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivityForResult(intent, SCANNING_QR_CODE);
            } else {
                showDialog("您不是本机构用户，请联系管理员");

            }
        }
    }
    private void requestCameraPermission() {

        requestPermissions(new String[]{Manifest.permission.CAMERA},1);
    }
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            if(granted){
                if (UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND) != null &&
                        UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getUserid() != 0) {

                    Intent intent = new Intent(getActivity(), MipcaActivityCapture.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivityForResult(intent, SCANNING_QR_CODE);
                } else {
                    showDialog("您不是本机构用户，请联系管理员");

                }
            }
        }
    }


    /**
     * 显示电子书
     */
    private void showElectronicBookFragment() {
        if (mLibraryElectronicBookFragment == null) {
            mLibraryElectronicBookFragment = new LibraryElectronicBookFragment();
        }

        mListener.onClickInstitutionLibraryList(1);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, mLibraryElectronicBookFragment).commit();
    }

    /**
     * 显示自有资源
     */
    private void showSelfResourceFragment() {

        mListener.onClickInstitutionLibraryList(2);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, LibraryResourceListFragment.newInstance(LibraryResourceListFragment.TYPE_SELF)).commit();
    }

    /**
     * 显示共享资源
     */
    private void showShareResourceFragment() {
        mListener.onClickInstitutionLibraryList(3);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, LibraryResourceListFragment.newInstance(LibraryResourceListFragment.TYPE_SHARE)).commit();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        builder.create();
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("图书馆首页");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("图书馆首页");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    private OnInstitutionLibraryListFragmentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnInstitutionLibraryListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnInstitutionLibraryListFragmentListener {
        void onClickInstitutionLibraryList(int i);
    }

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
