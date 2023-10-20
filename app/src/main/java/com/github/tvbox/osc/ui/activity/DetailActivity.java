package com.github.tvbox.osc.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.ClipData;
import com.github.tvbox.osc.util.thunder.Thunder;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.github.tvbox.osc.cache.CacheManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.bean.AbsXml;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.ui.adapter.SeriesAdapter;
import com.github.tvbox.osc.ui.adapter.SeriesFlagAdapter;
import com.github.tvbox.osc.ui.dialog.QuickSearchDialog;
import com.github.tvbox.osc.ui.fragment.PlayFragment;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.SearchHelper;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.jessyan.autosize.utils.AutoSizeUtils;

import android.graphics.Paint;
import android.text.TextPaint;
import androidx.annotation.NonNull;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */

public class DetailActivity extends BaseActivity {
    private LinearLayout llLayout;
    private FragmentContainerView llPlayerFragmentContainer;
    private View llPlayerFragmentContainerBlock;
    private View llPlayerPlace;
    private PlayFragment playFragment = null;
    private ImageView ivThumb;
    private TextView tvName;
    private TextView tvYear;
    private TextView tvSite;
    private TextView tvTag;
    private TextView tvArea;
    private TextView tvLang;
    private TextView tvType;
    private TextView tvActor;
    private TextView tvDirector;
    private TextView tvPlayUrl;
    private TextView tvDes;
    private TextView tvPlay;
    private TextView tvSort;
    private TextView tvQuickSearch;
    private TextView tvCollect;
    private TextView myPush;
    private TvRecyclerView mGridViewFlag;
    private TvRecyclerView mGridView;
    private TvRecyclerView mSeriesGroupView;
    private LinearLayout mEmptyPlayList;
    private SourceViewModel sourceViewModel;
    private Movie.Video mVideo;
    private VodInfo vodInfo;
    private SeriesFlagAdapter seriesFlagAdapter;
    private BaseQuickAdapter<String, BaseViewHolder> seriesGroupAdapter;
    private SeriesAdapter seriesAdapter;
    public String vodId;
    public String sourceKey;
    public String firstsourceKey;
    boolean seriesSelect = false;
    private View seriesFlagFocus = null;
    private boolean isReverse;
    private String preFlag="";
    private String wdName = "";
    private String wdPic = "";
    private boolean firstReverse;
    private V7GridLayoutManager mGridViewLayoutMgr = null;
    private HashMap<String, String> mCheckSources = null;
    private final ArrayList<String> seriesGroupOptions = new ArrayList<>();
    private View currentSeriesGroupView;
    private int GroupCount;
    private SourceBean cuHome;
    private String spName;
    private String spPic;
    private String spId;
    private String tokenInfo;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        initView();
        initViewModel();
        initData();
        toggleSubtitleTextSize();
    }

    public static void start(Activity activity, String key, String id, String name, String pic, boolean clean, boolean fullWindows) {
        Intent newIntent = new Intent(activity, DetailActivity.class);
        newIntent.putExtra("wdName", name);
        newIntent.putExtra("sourceKey", key);
        newIntent.putExtra("id", id);
        newIntent.putExtra("wdPic", pic);
        newIntent.putExtra("fullWindows", fullWindows);
        if(clean)newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(newIntent);
    }
    public static void start(Activity activity, String key, String id, String name, String pic,boolean clean) {
        start(activity, key, id, name, pic, clean, false);
    }
    public static void start(Activity activity, String key, String id, String name, String pic) {
        start(activity, key, id, name, pic, true, false);
    }
    public static void start(Activity activity,String keyIdName) {
        String [] arr = keyIdName.split(",");
        String key = arr[0];
        String id = arr[1];
        String name = "";
        if (arr.length > 2) name = arr[2];
        start(activity, key, id, name, "", true, true);
    }
    private void initView() {
        llLayout = findViewById(R.id.llLayout);
        llPlayerPlace = findViewById(R.id.previewPlayerPlace);
        llPlayerFragmentContainer = findViewById(R.id.previewPlayer);
        llPlayerFragmentContainerBlock = findViewById(R.id.previewPlayerBlock);
        ivThumb = findViewById(R.id.ivThumb);
        llPlayerPlace.setVisibility(showPreview ? View.VISIBLE : View.GONE);
        ivThumb.setVisibility(!showPreview ? View.VISIBLE : View.GONE);
        tvName = findViewById(R.id.tvName);
        tvYear = findViewById(R.id.tvYear);
        tvSite = findViewById(R.id.tvSite);
        tvTag = findViewById(R.id.tvTag);
        tvArea = findViewById(R.id.tvArea);
        tvLang = findViewById(R.id.tvLang);
        tvType = findViewById(R.id.tvType);
        tvActor = findViewById(R.id.tvActor);
        tvDirector = findViewById(R.id.tvDirector);
        tvPlayUrl = findViewById(R.id.tvPlayUrl);
        tvDes = findViewById(R.id.tvDes);
        tvPlay = findViewById(R.id.tvPlay);
        tvSort = findViewById(R.id.tvSort);
        tvCollect = findViewById(R.id.tvCollect);
        myPush = findViewById(R.id.myPush);
        tvQuickSearch = findViewById(R.id.tvQuickSearch);
        mEmptyPlayList = findViewById(R.id.mEmptyPlaylist);
        mGridView = findViewById(R.id.mGridView);
        mGridView.setHasFixedSize(true);
        mGridView.setHasFixedSize(false);
        this.mGridViewLayoutMgr = new V7GridLayoutManager(this.mContext, 6);
        mGridView.setLayoutManager(this.mGridViewLayoutMgr);
//        mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        seriesAdapter = new SeriesAdapter();
        mGridView.setAdapter(seriesAdapter);
        mGridViewFlag = findViewById(R.id.mGridViewFlag);
        mGridViewFlag.setHasFixedSize(true);
        mGridViewFlag.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        seriesFlagAdapter = new SeriesFlagAdapter();
        mGridViewFlag.setAdapter(seriesFlagAdapter);
        isReverse = false;
        firstReverse = false;
        preFlag = "";
        if (showPreview) {
            playFragment = new PlayFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.previewPlayer, playFragment).commit();
            getSupportFragmentManager().beginTransaction().show(playFragment).commitAllowingStateLoss();
            tvPlay.setText("全屏");
        }

        mSeriesGroupView = findViewById(R.id.mSeriesGroupView);
        mSeriesGroupView.setHasFixedSize(true);
        mSeriesGroupView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        seriesGroupAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_series_group, seriesGroupOptions) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                TextView tvSeries = helper.getView(R.id.tvSeriesGroup);
                tvSeries.setText(item);
            }
        };
        mSeriesGroupView.setAdapter(seriesGroupAdapter);

        //禁用播放地址焦点
        tvPlayUrl.setFocusable(false);
        llPlayerFragmentContainerBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApiConfig.isAli(spId)){
                    String endSP = sourceKey + "," + spId + "," + vodInfo.name;
                    Hawk.put(HawkConfig.MY_ENDSP, endSP);
                }
                toggleFullPreview();
            }
        });
        llPlayerFragmentContainerBlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean fbx = Hawk.get(HawkConfig.MY_BX, true);
                fbx = !fbx;
                Hawk.put(HawkConfig.MY_BX, fbx);
                String tip = "关闭";
                if(fbx) tip = "开启";
                alert("集数优化已"+tip);
                return true;
            }
        });

        tvSort.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (vodInfo != null && vodInfo.seriesMap.size() > 0) {
                    vodInfo.reverseSort = !vodInfo.reverseSort;
                    isReverse = !isReverse;
                    vodInfo.reverse();
                    vodInfo.playIndex=(vodInfo.seriesMap.get(vodInfo.playFlag).size()-1)-vodInfo.playIndex;
                    insertVod(sourceKey, vodInfo);
                    firstReverse = true;
                    setSeriesGroupOptions();
                    seriesAdapter.notifyDataSetChanged();
                }
            }
        });

        tvSort.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text = "r"+DefaultConfig.getHttpUrl(spName);
                updateData(text);
                return true;
            }
        });
        tvPlay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                /*String kv = "key:"+vodInfo.name +"$$$"+spId+"$$$"+ spPic+"$$$"+sourceKey;
                if(spId.contains("aliyundrive"))kv = vodInfo.name +" "+spId+" "+ spPic;
                updateData(kv);*/
                updateData("stoken");
                start(mActivity, sourceKey, spId, vodInfo.name, wdPic);
                return true;
            }
        });
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.MY_ENDSP, "");
                if (showPreview) {
                    toggleFullPreview();
                    if(firstReverse){
                        jumpToPlay();
                        firstReverse=false;
                    }
                } else {
                    jumpToPlay();
                }
            }
        });

        tvQuickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSearch();
                QuickSearchDialog quickSearchDialog = new QuickSearchDialog(DetailActivity.this);
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, quickSearchData));
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, quickSearchWord));
                quickSearchDialog.show();
                if (pauseRunnable != null && pauseRunnable.size() > 0) {
                    searchExecutorService = Executors.newFixedThreadPool(5);
                    for (Runnable runnable : pauseRunnable) {
                        searchExecutorService.execute(runnable);
                    }
                    pauseRunnable.clear();
                    pauseRunnable = null;
                }
                quickSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            if (searchExecutorService != null) {
                                pauseRunnable = searchExecutorService.shutdownNow();
                                searchExecutorService = null;
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            }
        });

        tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagInfo = vodInfo.tag;
                if (tokenInfo != null) {
                    tagInfo = tokenInfo;
                    copyInfo("已复制token","tokenInfo "+tagInfo);
                }
            }
        });
        tvType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String tagInfo = vodInfo.tag;
                if (tokenInfo != null) {
                    tagInfo = tokenInfo;
                    alert(tagInfo);
                }
                return true;
            }
        });
        /*tvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagInfo = vodInfo.tag;
                if (tokenInfo != null) {
                    tagInfo = tokenInfo;
                }
                copyInfo("已复制token",tagInfo);
            }
        });*/

        tvTag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CacheManager.delete(vodInfo.name,0);
                alert("已清空该缓存");
                return true;
            }
        });

        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvCollect.getText().toString();
                if ("加入收藏".equals(text)) {
                    RoomDataManger.insertVodCollect(sourceKey, vodInfo);
                    alert("已加入收藏夹");
                    tvCollect.setText("取消收藏");
                } else {
                    RoomDataManger.deleteVodCollect(sourceKey, vodInfo);
                    alert("已移除收藏夹");
                    tvCollect.setText("加入收藏");
                }
            }
        });
        tvCollect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String kv = vodInfo.name +" "+spId+" "+ spPic;
                updateData(kv);
                return true;
            }
        });

        tvSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiConfig.get().setSourceBean(cuHome);
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle bundle = new Bundle();
                bundle.putBoolean("useCache", true);
                intent.putExtras(bundle);
                DetailActivity.this.startActivity(intent);
            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInfo("已复制标题",vodInfo.name);
            }
        });

        tvDes.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //copyInfo("已复制站点信息",DefaultConfig.siteJson);
                Bundle bundle = new Bundle();
                bundle.putString("mypush", "yes");
                jumpActivity(PushActivity.class, bundle);
                return true;
            }
        });

        myPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheManager.delete(vodInfo.name,0);
                alert("已清空该缓存");
                String kv = "notip"+spId +" pic";
                updateData(kv);
                start(mActivity, sourceKey, spId, vodInfo.name, wdPic);
            }
        });

        myPush.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(tokenInfo==null) alert("没有token信息");
                else updateData("tokenInfo "+tokenInfo);
                return true;
            }
        });

        tvDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInfo("已复制", spName);
            }
        });
        tvDirector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyInfo("已复制链接",DefaultConfig.getHttpUrl(spName));
                return true;
            }
        });
        tvPlayUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyInfo("已复制", spName);
            }
        });
        tvPlayUrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyInfo("已复制链接",DefaultConfig.getHttpUrl(spName));
                return true;
            }
        });
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = false;
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = true;
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
            }
        });
        mGridViewFlag.setOnItemListener(new TvRecyclerView.OnItemListener() {
            private void refresh(View itemView, int position) {
                String newFlag = seriesFlagAdapter.getData().get(position).name;
                if (vodInfo != null && !vodInfo.playFlag.equals(newFlag)) {
                    for (int i = 0; i < vodInfo.seriesFlags.size(); i++) {
                        VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(i);
                        if (flag.name.equals(vodInfo.playFlag)) {
                            flag.selected = false;
                            seriesFlagAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(position);
                    flag.selected = true;
                    // clean pre flag select status
                    if (vodInfo.seriesMap.get(vodInfo.playFlag).size() > vodInfo.playIndex) {
                        vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = false;
                    }
                    vodInfo.playFlag = newFlag;
                    seriesFlagAdapter.notifyItemChanged(position);
                    refreshList();
                }
                seriesFlagFocus = itemView;
            }

            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
//                seriesSelect = false;
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                refresh(itemView, position);
//                if(isReverse)vodInfo.reverse();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                refresh(itemView, position);
//                if(isReverse)vodInfo.reverse();
            }
        });
        seriesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
                    boolean reload = false;
                    for (int j = 0; j < vodInfo.seriesMap.get(vodInfo.playFlag).size(); j++) {
                        seriesAdapter.getData().get(j).selected = false;
                        seriesAdapter.notifyItemChanged(j);
                    }
                    //解决倒叙不刷新
                    if (vodInfo.playIndex != position) {
                        seriesAdapter.getData().get(position).selected = true;
                        seriesAdapter.notifyItemChanged(position);
                        vodInfo.playIndex = position;

                        reload = true;
                    }
                    //解决当前集不刷新的BUG
                    if (!preFlag.isEmpty() && !vodInfo.playFlag.equals(preFlag)) {
                        reload = true;
                    }

                    seriesAdapter.getData().get(vodInfo.playIndex).selected = true;
                    seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                    //选集全屏 想选集不全屏的注释下面一行
                    if (showPreview && !fullWindows) toggleFullPreview();
                    if (!showPreview || reload) {
                        jumpToPlay();
                        firstReverse=false;
                    }
                }
            }
        });

        mSeriesGroupView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                TextView txtView = itemView.findViewById(R.id.tvSeriesGroup);
                txtView.setTextColor(Color.WHITE);
//                currentSeriesGroupView = null;
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                TextView txtView = itemView.findViewById(R.id.tvSeriesGroup);
                txtView.setTextColor(mContext.getResources().getColor(R.color.color_02F8E1));
                if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
                    int targetPos = position * GroupCount+1;
                    mGridView.smoothScrollToPosition(targetPos);
                }
                currentSeriesGroupView = itemView;
                currentSeriesGroupView.isSelected();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) { }
        });
        seriesGroupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                TextView newTxtView = view.findViewById(R.id.tvSeriesGroup);
                newTxtView.setTextColor(mContext.getResources().getColor(R.color.color_02F8E1));
                if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
                    int targetPos =  position * GroupCount+1;
//                    mGridView.scrollToPosition(targetPos);
                    mGridView.smoothScrollToPosition(targetPos);
                }
                if(currentSeriesGroupView != null) {
                    TextView txtView = currentSeriesGroupView.findViewById(R.id.tvSeriesGroup);
                    txtView.setTextColor(Color.WHITE);
                }
                currentSeriesGroupView = view;
                currentSeriesGroupView.isSelected();
            }
        });
        mGridView.setOnFocusChangeListener((view, b) -> onGridViewFocusChange(view, b));
        setLoadSir(llLayout);
    }

    public void copyInfo(String title,String text){
        if (text != null) {
            ClipboardManager cm = (ClipboardManager)getSystemService(mContext.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(null, text));
            alert(title);
        }
    }

    public static void copyInfo(Context context,String title,String text) {
        if (text != null) {
            ClipboardManager cm = (ClipboardManager)context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(null, text));
            alert(title);
        }
    }

    public static void alert(String msg) {
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }
    public static void updateData(String text) {
        String aliurl = "http://qyh.haocew.com/qy/demand/vd";
        String pwd = Hawk.get(HawkConfig.MY_PWD,"");
        if (!pwd.isEmpty()) {
            try {
                final boolean bflag = text.startsWith("notip");
                if(bflag)text=text.replace("notip","");
                text = ApiConfig.jkey + text;
                JSONObject jj = new JSONObject();
                jj.put("pwd", pwd);
                jj.put("key", text);
                OkGo.<String>post(aliurl).upJson(jj).execute(new AbsCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if(json!=null&&!json.equals("")) {
                            try {
                                JSONObject jo = new JSONObject(json);
                                String msg = jo.optString("msg", "失败");
                                if(!bflag) alert(msg);
                            } catch (Exception e) {
                            }
                        }
                    }
                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        return response.body().string();
                    }
                    @Override
                    public void onError(Response<String> response) {
                        alert("请求错误");
                        super.onError(response);
                    }
                });
            } catch (Exception e) {
            }
        }else {
            //alert("无权限");
        }
    }

    private void onGridViewFocusChange(View view, boolean hasFocus) {
        if (llPlayerFragmentContainerBlock.getVisibility() != View.VISIBLE) return;
        llPlayerFragmentContainerBlock.setFocusable(!hasFocus);
    }

    private void initCheckedSourcesForSearch() {
        mCheckSources = SearchHelper.getSourcesForSearch();
    }

    private List<Runnable> pauseRunnable = null;

    private void jumpToPlay() {
        if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
            preFlag = vodInfo.playFlag;
            //更新播放地址
            //setTextShow(tvPlayUrl, "播放地址：", vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).url);
            Bundle bundle = new Bundle();
            //保存历史
            insertVod(firstsourceKey, vodInfo);
            bundle.putString("sourceKey", sourceKey);
//            bundle.putSerializable("VodInfo", vodInfo);
            App.getInstance().setVodInfo(vodInfo);
            if (showPreview) {
                if (previewVodInfo == null) {
                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        oos.writeObject(vodInfo);
                        oos.flush();
                        oos.close();
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
                        previewVodInfo = (VodInfo) ois.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (previewVodInfo != null) {
                    previewVodInfo.playerCfg = vodInfo.playerCfg;
                    previewVodInfo.playFlag = vodInfo.playFlag;
                    previewVodInfo.playIndex = vodInfo.playIndex;
                    previewVodInfo.seriesMap = vodInfo.seriesMap;
//                    bundle.putSerializable("VodInfo", previewVodInfo);
                    App.getInstance().setVodInfo(previewVodInfo);
                }
                playFragment.setData(bundle);
            } else {
                jumpActivity(PlayActivity.class, bundle);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void refreshList() {
        if (vodInfo.seriesMap.get(vodInfo.playFlag).size() <= vodInfo.playIndex) {
            vodInfo.playIndex = 0;
        }

        if (vodInfo.seriesMap.get(vodInfo.playFlag) != null) {
            boolean canSelect = true;
            for (int j = 0; j < vodInfo.seriesMap.get(vodInfo.playFlag).size(); j++) {
                if(vodInfo.seriesMap.get(vodInfo.playFlag).get(j).selected){
                    canSelect = false;
                    break;
                }
            }
            if(canSelect)vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = true;
        }

        Paint pFont = new Paint();
//        pFont.setTypeface(Typeface.DEFAULT );
        Rect rect = new Rect();

        List<VodInfo.VodSeries> list = vodInfo.seriesMap.get(vodInfo.playFlag);
        int listSize = list.size();
        int w = 1;
        for(int i =0; i < listSize; ++i){
            String name = list.get(i).name;
            pFont.getTextBounds(name, 0, name.length(), rect);
            if(w < rect.width()){
                w = rect.width();
            }
        }
        w += 32;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth()/3;
        int offset = screenWidth/w;
        if(offset <=2) offset =2;
        if(offset > 6) offset =6;
        mGridViewLayoutMgr.setSpanCount(offset);
        seriesAdapter.setNewData(vodInfo.seriesMap.get(vodInfo.playFlag));
        setSeriesGroupOptions();
        mGridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGridView.smoothScrollToPosition(vodInfo.playIndex);
            }
        }, 100);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setSeriesGroupOptions(){
        List<VodInfo.VodSeries> list = vodInfo.seriesMap.get(vodInfo.playFlag);
        int listSize = list.size();
        int offset = mGridViewLayoutMgr.getSpanCount();
        seriesGroupOptions.clear();
        GroupCount=(offset==3 || offset==6)?30:20;
        if(listSize>100 && listSize<=400)GroupCount=60;
        if(listSize>400)GroupCount=120;
        if(listSize > GroupCount) {
            mSeriesGroupView.setVisibility(View.VISIBLE);
            int remainedOptionSize = listSize % GroupCount;
            int optionSize = listSize / GroupCount;

            for(int i = 0; i < optionSize; i++) {
                if(vodInfo.reverseSort)
//                    seriesGroupOptions.add(String.format("%d - %d", i * GroupCount + GroupCount, i * GroupCount + 1));
                    seriesGroupOptions.add(String.format("%d - %d", listSize - (i * GroupCount + 1)+1, listSize - (i * GroupCount + GroupCount)+1));
                else
                    seriesGroupOptions.add(String.format("%d - %d", i * GroupCount + 1, i * GroupCount + GroupCount));
            }
            if(remainedOptionSize > 0) {
                if(vodInfo.reverseSort)
//                    seriesGroupOptions.add(String.format("%d - %d", optionSize * GroupCount + remainedOptionSize, optionSize * GroupCount + 1));
                    seriesGroupOptions.add(String.format("%d - %d", listSize - (optionSize * GroupCount + 1)+1, listSize - (optionSize * GroupCount + remainedOptionSize)+1));
                else
                    seriesGroupOptions.add(String.format("%d - %d", optionSize * GroupCount + 1, optionSize * GroupCount + remainedOptionSize));
            }
//            if(vodInfo.reverseSort) Collections.reverse(seriesGroupOptions);

            seriesGroupAdapter.notifyDataSetChanged();
        }else {
            mSeriesGroupView.setVisibility(View.GONE);
        }
    }

    private void setTextShow(TextView view, String tag, String info) {
        if (info == null || info.trim().isEmpty()|| info.equals("无")) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        view.setText(Html.fromHtml(getHtml(tag, info)));
    }

    private String removeHtmlTag(String info) {
        if (info == null)
            return "";
        return info.replaceAll("\\<.*?\\>", "").replaceAll("\\s", "");
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.detailResult.observe(this, new Observer<AbsXml>() {
            @Override
            public void onChanged(AbsXml absXml) {
                try {
                    if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                        showSuccess();
                        if(!TextUtils.isEmpty(absXml.msg) && !absXml.msg.equals("数据列表")){
                            Toast.makeText(DetailActivity.this, absXml.msg, Toast.LENGTH_SHORT).show();
                            showEmpty();
                            return;
                        }
                        mVideo = absXml.movie.videoList.get(0);
                        if(mVideo!=null&&mVideo.tag!=null&&!mVideo.tag.isEmpty()){
                            String[] tagArr = mVideo.tag.split(";");
                            if(tagArr.length>1) {
                                tokenInfo = tagArr[1];
                                if (tokenInfo.length() > 160) {
                                    String otokenInfo = Hawk.get(HawkConfig.MY_TOKENINFO, "");
                                    JSONObject jo = new JSONObject(tokenInfo);
                                    if(otokenInfo.isEmpty()||(!jo.optString("accessTokenOpen", "").isEmpty())){
                                        Hawk.put(HawkConfig.MY_TOKENINFO, tokenInfo);
                                    }
                                }
                            }
                        }
                        if (TextUtils.isEmpty(mVideo.name))mVideo.name = "片名";
                        Movie.Video mvo=(Movie.Video)CacheManager.getCache(mVideo.name);
                        boolean spflag = mVideo.director==null||mVideo.director.isEmpty();
                        if (mvo != null&&spflag) {
                            mVideo.pic = mvo.pic;
                            mVideo.tag = mvo.tag;
                            mVideo.year = mvo.year;
                            mVideo.area = mvo.area;
                            mVideo.director = mvo.director;
                            mVideo.actor = mvo.actor;
                            mVideo.des = mvo.des;
                        }
                        String tagInfo = "";
                        if(mVideo.tag!=null&&!mVideo.tag.isEmpty()){
                            String[] tagArr = mVideo.tag.split(";");
                            tagInfo = tagArr[0];
                        }
                        if (mvo == null&&!spflag) {
                            mvo = new Movie.Video();
                            mvo.pic = mVideo.pic;
                            mvo.tag = mVideo.tag;
                            mvo.year = mVideo.year;
                            mvo.area = mVideo.area;
                            mvo.director = mVideo.director;
                            mvo.actor = mVideo.actor;
                            mvo.des = mVideo.des;
                            if (!tagInfo.contains("分")) CacheManager.save(mVideo.name, mvo);
                        }
                        spflag = mVideo.director==null||mVideo.director.isEmpty();
                        vodInfo = new VodInfo();
                        vodInfo.setVideo(mVideo);
                        vodInfo.sourceKey = firstsourceKey;
                        tvName.setText(mVideo.name);
                        cuHome = ApiConfig.get().getSource(firstsourceKey);
                        if (!tagInfo.isEmpty()&&tagInfo.contains("分")) {
                            String score = null, jsnum = null;
                            String [] tagArr = tagInfo.split("评分：");
                            tagInfo = tagArr[0];
                            String tags = tagArr[1];
                            String [] spArr = tags.split(" ");
                            score = spArr[0];
                            if(spArr.length>1) jsnum = spArr[1];
                            setTextShow(tvArea, "评分：", score);
                            setTextShow(tvLang, "集数：", jsnum);
                            mvo.note=score+" / "+jsnum;
                            CacheManager.save(mVideo.name, mvo);
                        }else {
                            setTextShow(tvArea, "地区：", mVideo.area);
                            setTextShow(tvLang, "语言：", mVideo.lang);
                        }
                        setTextShow(tvSite, "来源：", cuHome.getName());
                        setTextShow(tvYear, "上映：", mVideo.year);
                        if (!firstsourceKey.equals(sourceKey)) {
                            setTextShow(tvType, "类型：", "[" + ApiConfig.get().getSource(sourceKey).getName() + "] 解析");
                        } else {
                            setTextShow(tvType, "类型：", mVideo.type);
                        }
                        setTextShow(tvTag, "标签：", tagInfo);
                        setTextShow(tvActor, "演员：", mVideo.actor);
                        setTextShow(tvDirector, "导演：", mVideo.director);
                        setTextShow(tvDes, "内容简介：", removeHtmlTag(mVideo.des));
                        if (!TextUtils.isEmpty(mVideo.pic)) {
                            Picasso.get()
                                    .load(DefaultConfig.checkReplaceProxy(mVideo.pic))
                                    .transform(new RoundTransformation(MD5.string2MD5(mVideo.pic + mVideo.name))
                                            .centerCorp(true)
                                            .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                            .roundRadius(AutoSizeUtils.mm2px(mContext, 10), RoundTransformation.RoundType.ALL))
                                    .placeholder(R.drawable.img_loading_placeholder)
                                    .noFade()
                                    .error(R.drawable.img_loading_placeholder)
                                    .into(ivThumb);
                        } else {
                            ivThumb.setImageResource(R.drawable.img_loading_placeholder);
                        }
                        if (vodInfo.seriesMap != null && vodInfo.seriesMap.size() > 0) {
                            if (vodInfo.id != null) {
                                String bfurl = "";
                                if(vodInfo.seriesMap.get(vodInfo.playFlag)!=null)
                                    bfurl = vodInfo.name+" "+vodInfo.seriesMap.get(vodInfo.playFlag).get(0).url;
                                String[] idInfo = vodInfo.id.split("\\$\\$\\$");
                                String _bfurl = idInfo[0];
                                if(_bfurl.contains("aliyundrive")){
                                    _bfurl = _bfurl.replaceAll(".*(http.*)", "$1");
                                    bfurl = vodInfo.name+" "+_bfurl;
                                }
                                spId = _bfurl;
                                vodInfo.id = spId;
                                if(bfurl.isEmpty())bfurl = vodInfo.name+" "+_bfurl;
                                spName = bfurl;
                                //设置播放地址
                                if(spflag)setTextShow(tvPlayUrl, "视频信息：", bfurl);
                                else setTextShow(tvPlayUrl, null, null);
                            }

                            if (mVideo.pic != null) {
                                if (ApiConfig.isPic(mVideo.pic)&&mVideo.pic.contains("doubanio")) {
                                    spPic = mVideo.pic;
                                    if(sourceKey.equals("push_agentqq")&&!wdPic.contains(spPic)&&!spPic.contains("@")){
                                        updateData("notip" + spId + " " + spPic);
                                    }
                                    wdPic = spPic;
                                }
                            }else {
                                vodInfo.pic = wdPic;
                                spPic = wdPic;
                            }

                            mGridViewFlag.setVisibility(View.VISIBLE);
                            mGridView.setVisibility(View.VISIBLE);
                            tvPlay.setVisibility(View.VISIBLE);
                            mEmptyPlayList.setVisibility(View.GONE);
                            VodInfo vodInfoRecord = getRoomData(vodInfo);

                            // 读取历史记录
                            if (vodInfoRecord != null) {
                                vodInfo.playIndex = Math.max(vodInfoRecord.playIndex, 0);
                                vodInfo.playFlag = vodInfoRecord.playFlag;
                                vodInfo.playerCfg = vodInfoRecord.playerCfg;
                                vodInfo.reverseSort = vodInfoRecord.reverseSort;
                                vodInfo.progressKey = vodInfoRecord.progressKey;
                                App.getInstance().setVodInfo(vodInfo);
                            } else {
                                vodInfo.playIndex = 0;
                                vodInfo.playFlag = null;
                                vodInfo.playerCfg = "";
                                vodInfo.reverseSort = false;
                            }

                            if (vodInfo.reverseSort) {
                                vodInfo.reverse();
                            }
                            if (vodInfo.playFlag == null || !vodInfo.seriesMap.containsKey(vodInfo.playFlag))
                                vodInfo.playFlag = (String) vodInfo.seriesMap.keySet().toArray()[0];

                            int flagScrollTo = 0;
                            for (int j = 0; j < vodInfo.seriesFlags.size(); j++) {
                                VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(j);
                                if (flag.name.equals(vodInfo.playFlag)) {
                                    flagScrollTo = j;
                                    flag.selected = true;
                                } else
                                    flag.selected = false;
                            }
                            seriesFlagAdapter.setNewData(vodInfo.seriesFlags);
                            mGridViewFlag.scrollToPosition(flagScrollTo);

                            refreshList();
                            if (showPreview) {
                                jumpToPlay();
                                llPlayerFragmentContainer.setVisibility(View.VISIBLE);
                                llPlayerFragmentContainerBlock.setVisibility(View.VISIBLE);
                            }
                            if(fullWindows) {
                                fullWindows = false;
                                toggleFullPreview();
                            }
                            // startQuickSearch();
                        } else {
                            mGridViewFlag.setVisibility(View.GONE);
                            mGridView.setVisibility(View.GONE);
                            mSeriesGroupView.setVisibility(View.GONE);
                            tvPlay.setVisibility(View.GONE);
                            mEmptyPlayList.setVisibility(View.VISIBLE);
                        }
                    } else {
                        showEmpty();
                        llPlayerFragmentContainer.setVisibility(View.GONE);
                        llPlayerFragmentContainerBlock.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    alert("错误信息de："+e.getMessage());
                }
            }
        });
    }

    public VodInfo getRoomData(VodInfo info){
        VodInfo vodInfoRecord = RoomDataManger.getVodInfo(info.sourceKey, info.id);
        if (vodInfoRecord == null&& info.id.contains("aliyundrive")) {
            List<VodInfo> allVodRecord = RoomDataManger.getAllVodRecord(100);
            List<VodInfo> vodInfoList = new ArrayList<>();
            VodInfo sinfo = null;
            for (VodInfo vInfo : allVodRecord) {
                if (vInfo.name.equals(info.name)) {
                    sinfo = vInfo;
                    sinfo.progressKey = ApiConfig.getProgressKey(sinfo);
                    break;
                }
            }
            return sinfo;
        }else {
            return vodInfoRecord;
        }
    }

    private String getHtml(String label, String content) {
        if (content == null) {
            content = "";
        }
        return label + "<font color=\"#FFFFFF\">" + content + "</font>";
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            wdName = bundle.getString("wdName", "");
            wdPic = bundle.getString("wdPic", "");
            if(!wdPic.isEmpty()) wdPic = wdPic.split("\\?")[0];
            spId = bundle.getString("id", null);
            sourceKey = bundle.getString("sourceKey", "");
            fullWindows = bundle.getBoolean("fullWindows", false);
            if(!wdName.isEmpty())wdName = wdName.split("\\?")[0];
            if(sourceKey.equals("push_agentqq")){
                String[] idInfo = spId.split(",");
                if(idInfo.length>1){
                    spId = idInfo[0];
                    sourceKey = idInfo[1];
                }
            }
            loadDetail(spId, sourceKey);
        }
    }

    private void loadDetail(String vid, String key) {
        if (vid != null) {
            vodId = vid;
            sourceKey = key;
            firstsourceKey = key;
            showLoading();
            sourceViewModel.getDetail(sourceKey, vodId,wdName);
            boolean isVodCollect = RoomDataManger.isVodCollect(sourceKey, vodId);
            if (isVodCollect) {
                tvCollect.setText("取消收藏");
            } else {
                tvCollect.setText("加入收藏");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_REFRESH) {
            if (event.obj != null) {
                if (event.obj instanceof Integer) {
                    int index = (int) event.obj;
                    for (int j = 0; j < vodInfo.seriesMap.get(vodInfo.playFlag).size(); j++) {
                        seriesAdapter.getData().get(j).selected = false;
                        seriesAdapter.notifyItemChanged(j);
                    }
                    seriesAdapter.getData().get(index).selected = true;
                    seriesAdapter.notifyItemChanged(index);
                    mGridView.setSelection(index);
                    vodInfo.playIndex = index;
                    //保存历史
                    insertVod(firstsourceKey, vodInfo);
                } else if (event.obj instanceof JSONObject) {
                    vodInfo.playerCfg = ((JSONObject) event.obj).toString();
                    //保存历史
                    insertVod(firstsourceKey, vodInfo);
                }

            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_SELECT) {
            if (event.obj != null) {
                Movie.Video video = (Movie.Video) event.obj;
                loadDetail(video.id, video.sourceKey);
            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_WORD_CHANGE) {
            if (event.obj != null) {
                String word = (String) event.obj;
                switchSearchWord(word);
            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_RESULT) {
            try {
                searchData(event.obj == null ? null : (AbsXml) event.obj);
            } catch (Exception e) {
                searchData(null);
            }
        }
    }

    private String searchTitle = "";
    private boolean hadQuickStart = false;
    private final List<Movie.Video> quickSearchData = new ArrayList<>();
    private final List<String> quickSearchWord = new ArrayList<>();
    private ExecutorService searchExecutorService = null;

    private void switchSearchWord(String word) {
        OkGo.getInstance().cancelTag("quick_search");
        quickSearchData.clear();
        searchTitle = word;
        searchResult();
    }

    private void startQuickSearch() {
        initCheckedSourcesForSearch();
        if (hadQuickStart)
            return;
        hadQuickStart = true;
        OkGo.getInstance().cancelTag("quick_search");
        quickSearchWord.clear();
        searchTitle = wdName;
        quickSearchData.clear();
        quickSearchWord.addAll(SearchHelper.splitWords(searchTitle));
        // 分词
        OkGo.<String>get("http://api.pullword.com/get.php?source=" + URLEncoder.encode(searchTitle) + "&param1=0&param2=0&json=1")
                .tag("fenci")
                .execute(new AbsCallback<String>() {
                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        if (response.body() != null) {
                            return response.body().string();
                        } else {
                            throw new IllegalStateException("网络请求错误");
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        try {
                            for (JsonElement je : new Gson().fromJson(json, JsonArray.class)) {
                                quickSearchWord.add(je.getAsJsonObject().get("t").getAsString());
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                        List<String> words = new ArrayList<>(new HashSet<>(quickSearchWord));
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, words));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

        searchResult();
    }

    private void searchResult() {
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        searchExecutorService = Executors.newFixedThreadPool(5);
        List<SourceBean> searchRequestList = new ArrayList<>();
        searchRequestList.addAll(ApiConfig.get().getSourceBeanList());
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        searchRequestList.remove(home);
        searchRequestList.add(0, home);

        ArrayList<String> siteKey = new ArrayList<>();
        for (SourceBean bean : searchRequestList) {
            if (!bean.isSearchable() || !bean.isQuickSearch()) {
                continue;
            }
            if (mCheckSources != null && !mCheckSources.containsKey(bean.getKey())) {
                continue;
            }
            siteKey.add(bean.getKey());
        }
        for (String key : siteKey) {
            searchExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sourceViewModel.getQuickSearch(key, searchTitle);
                }
            });
        }
    }

    private void searchData(AbsXml absXml) {
        if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
            List<Movie.Video> data = new ArrayList<>();
            for (Movie.Video video : absXml.movie.videoList) {
                // 去除当前相同的影片
                if (video.sourceKey.equals(sourceKey) && video.id.equals(vodId))
                    continue;
                data.add(video);
            }
            quickSearchData.addAll(data);
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, data));
        }
    }

    private void insertVod(String sourceKey, VodInfo vodInfo) {
        try {
            //if(ApiConfig.isPic(wdPic)&&!sourceKey.equals("push_agentqq"))vodInfo.pic = wdPic;
            vodInfo.playNote = vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).name;
        } catch (Throwable th) {
            vodInfo.playNote = "";
        }
        RoomDataManger.insertVodRecord(sourceKey, vodInfo);
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_HISTORY_REFRESH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        OkGo.getInstance().cancelTag("fenci");
        OkGo.getInstance().cancelTag("detail");
        OkGo.getInstance().cancelTag("quick_search");
        EventBus.getDefault().unregister(this);
        if (!showPreview) Thunder.stop(true);
    }

    @Override
    public void onBackPressed() {
        ApiConfig.zcount =10;
        alert(ApiConfig.zcount);
        if (fullWindows) {
            if (playFragment.onBackPressed())
                return;
            toggleFullPreview();
            mGridView.requestFocus();
            List<VodInfo.VodSeries> list = vodInfo.seriesMap.get(vodInfo.playFlag);
            //mSeriesGroupView.setVisibility(list.size()>GroupCount ? View.VISIBLE : View.GONE);
            return;
        }
        if (seriesSelect) {
            if (seriesFlagFocus != null && !seriesFlagFocus.isFocused()) {
                seriesFlagFocus.requestFocus();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && playFragment != null && fullWindows) {
            if (playFragment.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_MENU) {
            tvCollect.requestFocus();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // preview
    VodInfo previewVodInfo = null;
    boolean showPreview = Hawk.get(HawkConfig.SHOW_PREVIEW, true);; // true 开启 false 关闭
    boolean fullWindows = false;
    ViewGroup.LayoutParams windowsPreview = null;
    ViewGroup.LayoutParams windowsFull = null;

    void toggleFullPreview() {
        if (windowsPreview == null) {
            windowsPreview = llPlayerFragmentContainer.getLayoutParams();
        }
        if (windowsFull == null) {
            windowsFull = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        fullWindows = !fullWindows;
        llPlayerFragmentContainer.setLayoutParams(fullWindows ? windowsFull : windowsPreview);
        llPlayerFragmentContainerBlock.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        mGridView.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        mGridViewFlag.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        //mSeriesGroupView.setVisibility(fullWindows ? View.GONE : View.VISIBLE);

        //全屏下禁用详情页几个按键的焦点 防止上键跑过来
        tvPlay.setFocusable(!fullWindows);
        tvSort.setFocusable(!fullWindows);
        tvCollect.setFocusable(!fullWindows);
        tvQuickSearch.setFocusable(!fullWindows);
        toggleSubtitleTextSize();
    }

    void toggleSubtitleTextSize() {
        if (ApiConfig.subflag) {
            int subtitleTextSize  = Hawk.get(HawkConfig.MY_CURSIZE, 16);
            if (!fullWindows) {
                subtitleTextSize *= 0.6;
            }
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SUBTITLE_SIZE_CHANGE, subtitleTextSize));
        }
    }
}
