package com.lease.framework.biz.test.frg;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.lease.framework.biz.test.R;
import com.lease.fw.router.param.BindParam;
import com.lease.fw.ui.base.BaseViewModel;
import com.lease.fw.ui.frg.TitleFragment;
import com.lease.fw.ui.toast.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * created time: 2019-11-13
 * author: cqt
 * description: 参数传递测试
 */
public class ParamTestFragment extends TitleFragment<BaseViewModel> {

    @BindParam("message")
    public String msg = "";

    @BindParam("list")
    public ArrayList<String> msgs;

    @BindParam("sEntity")
    public SEntity sEntity;

    @BindParam("pEntity")
    public PEntity pEntity;

    @BindParam("pEntitys")
    public ArrayList<PEntity> pEntities;

    @Override
    protected int obtainContentLayout() {
        return R.layout.frg_param;
    }

    @Override
    protected void setupView() {
        super.setupView();
        setTitle("参数测试");

        getView().findViewById(R.id.btn_param).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(msg);
//                ToastUtil.show(msgs.get(new Random().nextInt(msgs.size())));
//                ToastUtil.show(sEntity.getName());
//                ToastUtil.show(pEntity.getName());
//                ToastUtil.show(pEntities.get(new Random().nextInt(pEntities.size())).getName());

//                Router.callBackResult(ParamTestFragment.this, "callback");
            }
        });
    }

    public static class SEntity implements Serializable {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class PEntity implements Parcelable {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public PEntity() {
        }

        protected PEntity(Parcel in) {
            this.name = in.readString();
        }

        public static final Creator<PEntity> CREATOR = new Creator<PEntity>() {
            @Override
            public PEntity createFromParcel(Parcel source) {
                return new PEntity(source);
            }

            @Override
            public PEntity[] newArray(int size) {
                return new PEntity[size];
            }
        };
    }
}