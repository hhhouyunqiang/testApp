package com.ringdata.ringinterview.capi.components.ui.survey.sample.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ringdata.base.delegates.LatteDelegate;
import com.ringdata.ringinterview.capi.components.R;
import com.ringdata.ringinterview.capi.components.constant.SPKey;
import com.ringdata.ringinterview.capi.components.data.dao.SampleAddressDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleContactDao;
import com.ringdata.ringinterview.capi.components.data.dao.SampleTouchDao;
import com.ringdata.ringinterview.capi.components.data.model.SampleAddress;
import com.ringdata.ringinterview.capi.components.data.model.SampleContact;
import com.ringdata.ringinterview.capi.components.data.model.SampleTouch;
import com.ringdata.ringinterview.capi.components.utils.AppUtil;

import java.util.List;

/**
 * Created by bella_wang on 2020/03/30.
 */

public class SampleDetailAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private final LatteDelegate delegate;
    private Integer userId = SPUtils.getInstance().getInt(SPKey.USERID);

    public SampleDetailAdapter(List<MultiItemEntity> data, LatteDelegate delegate) {
        super(data);
        this.delegate = delegate;
        addItemType(SampleDetailItemType.LIST_BEAN, R.layout.item_property_list);
        addItemType(SampleDetailItemType.LIST_EDIT_BEAN, R.layout.item_edit_property_list);
        addItemType(SampleDetailItemType.LIST_TITLE_BEAN, R.layout.item_property_title_list);
        addItemType(SampleDetailItemType.DIVIDER_40, R.layout.item_divider);

        addItemType(SampleDetailItemType.ADDRESS, R.layout.item_sample_address);
        addItemType(SampleDetailItemType.CONTACT, R.layout.item_sample_contact);
        addItemType(SampleDetailItemType.TOUCH, R.layout.item_sample_touch);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SampleDetailItemType.LIST_BEAN:
                final PropertyListBean listBean = (PropertyListBean) item;
                helper.setText(R.id.tv_item_left, Html.fromHtml(listBean.getLeft()));
                helper.setText(R.id.tv_item_text, listBean.getText());
                TextView rightIcon = helper.getView(R.id.tv_item_right);
                if (listBean.isRight()) {
                    rightIcon.setVisibility(View.VISIBLE);
                    if (listBean.getLeft().equals("样本地址")) {
                        rightIcon.setText("{icon-map}");
                        rightIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                baiduMapNavi(listBean.getText());
                            }
                        });
                    }
                } else {
                    rightIcon.setVisibility(View.GONE);
                }
                break;
            case SampleDetailItemType.LIST_EDIT_BEAN:
                PropertyListBean listBean2 = (PropertyListBean) item;
                helper.setText(R.id.tv_item_left, Html.fromHtml(listBean2.getLeft()));
                EditText textView = helper.getView(R.id.tv_item_text);
                textView.setText(listBean2.getText());
                TextView rightIcon2 = helper.getView(R.id.tv_item_right);
                if (listBean2.isRight()) {
                    rightIcon2.setVisibility(View.VISIBLE);
                } else {
                    rightIcon2.setVisibility(View.GONE);
                }
                break;
            case SampleDetailItemType.LIST_TITLE_BEAN:
                final PropertyListBean listBean3 = (PropertyListBean) item;
                helper.setText(R.id.tv_item_left, Html.fromHtml(listBean3.getLeft()));
                LinearLayout view_edit = helper.getView(R.id.ll_sample_detail_edit);
                LinearLayout view_delete = helper.getView(R.id.ll_sample_detail_delete);
                if (listBean3.isRight()) {
                    view_edit.setVisibility(View.VISIBLE);
                    view_delete.setVisibility(View.VISIBLE);
                    view_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listBean3.getTitleType() == SampleDetailItemType.ADDRESS) {
                                SampleAddress sampleAddress = (SampleAddress) listBean3.getItem();
                                delegate.start(EditAddressDelegate.newInstance(sampleAddress));
                            } else if (listBean3.getTitleType() == SampleDetailItemType.CONTACT) {
                                SampleContact sampleContact = (SampleContact) listBean3.getItem();
                                delegate.start(EditContactDelegate.newInstance(sampleContact));
                            } else if (listBean3.getTitleType() == SampleDetailItemType.TOUCH) {
                                SampleTouch sampleTouch = (SampleTouch) listBean3.getItem();
                                delegate.start(EditTouchDelegate.newInstance(sampleTouch));
                            }
                        }
                    });
                    view_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listBean3.getTitleType() == SampleDetailItemType.ADDRESS) {
                                final SampleAddress sampleAddress = (SampleAddress) listBean3.getItem();
                                AlertDialog dialog = new AlertDialog.Builder(delegate.getContext())
                                        .setTitle("确定")
                                        .setMessage("确定删除地址-(" + sampleAddress.getName() + ")？")
                                        .setCancelable(false)
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SampleAddressDao.deleteByAddressId(sampleAddress.getId(), userId);
                                            }
                                        }).show();
                            } else if (listBean3.getTitleType() == SampleDetailItemType.CONTACT) {
                                final SampleContact sampleContact = (SampleContact) listBean3.getItem();
                                AlertDialog dialog = new AlertDialog.Builder(delegate.getContext())
                                        .setTitle("确定")
                                        .setMessage("确定删除联系人-(" + sampleContact.getName() + ")？")
                                        .setCancelable(false)
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SampleContactDao.deleteByContactId(sampleContact.getId(), userId);
                                            }
                                        }).show();
                            } else if (listBean3.getTitleType() == SampleDetailItemType.TOUCH) {
                                final SampleTouch sampleTouch = (SampleTouch) listBean3.getItem();
                                AlertDialog dialog = new AlertDialog.Builder(delegate.getContext())
                                        .setTitle("确定")
                                        .setMessage("确定删除接触记录-(" + "" + ")？")
                                        .setCancelable(false)
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SampleTouchDao.deleteByTouchId(sampleTouch.getId(), userId);
                                            }
                                        }).show();
                            }
                        }
                    });
                } else {
                    view_edit.setVisibility(View.GONE);
                    view_delete.setVisibility(View.GONE);
                }
                break;
            case SampleDetailItemType.ADDRESS:
                SampleAddress sampleAddress = (SampleAddress) item;
                helper.setText(R.id.tv_item_sample_address_name, sampleAddress.getName());
                helper.setText(R.id.tv_item_sample_address_province, sampleAddress.getProvince());
                helper.setText(R.id.tv_item_sample_address_city, sampleAddress.getCity());
                helper.setText(R.id.tv_item_sample_address_district, sampleAddress.getDistrict());
                helper.setText(R.id.tv_item_sample_address_town, sampleAddress.getTown());
                helper.setText(R.id.tv_item_sample_address_address, sampleAddress.getAddress());
                helper.setText(R.id.tv_item_sample_address_description, sampleAddress.getDescription());
                break;
            case SampleDetailItemType.CONTACT:
                SampleContact sampleContact = (SampleContact) item;
                helper.setText(R.id.tv_item_sample_contact_name, sampleContact.getName());
                helper.setText(R.id.tv_item_sample_contact_relation, sampleContact.getRelation());
                helper.setText(R.id.tv_item_sample_contact_phone, sampleContact.getPhone());
                helper.setText(R.id.tv_item_sample_contact_mobile, sampleContact.getMobile());
                helper.setText(R.id.tv_item_sample_contact_email, sampleContact.getEmail());
                helper.setText(R.id.tv_item_sample_contact_qq, sampleContact.getQq());
                helper.setText(R.id.tv_item_sample_contact_weixin, sampleContact.getWeixin());
                helper.setText(R.id.tv_item_sample_contact_weibo, sampleContact.getWeibo());
                helper.setText(R.id.tv_item_sample_contact_description, sampleContact.getDescription());
                break;
            case SampleDetailItemType.TOUCH:
                SampleTouch sampleTouch = (SampleTouch) item;
                helper.setText(R.id.tv_item_sample_touch_type, sampleTouch.getType());
                helper.setText(R.id.tv_item_sample_touch_status, AppUtil.getSampleTouchStatus(sampleTouch.getStatus()));
                helper.setText(R.id.tv_item_sample_touch_description, sampleTouch.getDescription());
                break;
            default:
                break;
        }
    }

    public void baiduMapNavi(String address) {
        if (AppUtil.isPackageInstalled("com.baidu.BaiduMap")) {
            //调起百度导航
            Intent intent = new Intent();
            intent.setData(Uri.parse("baidumap://map/direction?" +
                    "destination=name:" + address +
                    "&mode=driving"));
            delegate.getContext().startActivity(intent);
        } else {
            ToastUtils.setBgColor(Color.GRAY);
            ToastUtils.showShort("您尚未安装百度地图APP");
        }
    }
}
