package day.cloudy.apps.tiles.activity.slide;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;

import java.util.List;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;
import day.cloudy.apps.tiles.adapter.AccessPointAdapter;
import day.cloudy.apps.tiles.adapter.OnItemClickListener;
import day.cloudy.apps.tiles.dialog.IpRequestDialog;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static butterknife.ButterKnife.findById;

/**
 * Created by Gaelan Bolger on 12/23/2016.
 * Intro slide for displaying network attached Hue Bridges
 */
public class AccessPointSlide extends Fragment implements ISlidePolicy {

    public interface OnAccessPointSelectedListener {
        void onAccessPointSelected(PHAccessPoint accessPoint);
    }

    @BindView(R.id.text_view_slide_title)
    TextView vTitle;
    @BindView(R.id.text_view_slide_blurb)
    TextView vBlurb;
    @BindView(R.id.recycler_view)
    RecyclerView vRecyclerView;
    @BindView(R.id.progress_bar)
    View vProgress;
    @BindView(R.id.layout_connect_manually)
    View vConnectManually;

    private OnAccessPointSelectedListener mListener;
    private AccessPointAdapter mAdapter;
    private RecyclerView.AdapterDataObserver mEmptyObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            vProgress.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    };

    public static AccessPointSlide newInstance() {
        return new AccessPointSlide();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AccessPointAdapter(getActivity(), new OnItemClickListener<PHAccessPoint>() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, PHAccessPoint item) {
                Timber.d("onItemClick: ");
                if (null != mListener)
                    mListener.onAccessPointSelected(item);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_base, container, false);
        inflater.inflate(R.layout.slide_content_ap_select, (ViewGroup) findById(view, R.id.frame_bottom), true);
        bind(this, view);

        vTitle.setText(R.string.slide_select_bridge_title);
        vBlurb.setText(R.string.slide_select_bridge_blurb);

        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        vRecyclerView.setAdapter(mAdapter);

        vConnectManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IpRequestDialog.newInstance(new IpRequestDialog.Listener() {
                    @Override
                    public void onIpValidated(String ipAddress) {
                        PHAccessPoint accessPoint = new PHAccessPoint();
                        accessPoint.setIpAddress(ipAddress);
                        accessPoint.setUsername(getString(R.string.app_name));
                        if (null != mListener)
                            mListener.onAccessPointSelected(accessPoint);
                    }
                }).show(getChildFragmentManager(), "ip_request");
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.registerAdapterDataObserver(mEmptyObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.unregisterAdapterDataObserver(mEmptyObserver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnAccessPointSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AccessPointSlide.OnAccessPointSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean isPolicyRespected() {
        return false; // advanced in activity
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        Toast.makeText(getActivity(), R.string.slide_select_bridge_toast, Toast.LENGTH_SHORT).show();
    }

    public void doBridgeSearch() {
        setAccessPoints(null);
        vConnectManually.setEnabled(false);
        PHHueSDK hueSDK = PHHueSDK.getInstance();
        PHBridgeSearchManager searchManager = (PHBridgeSearchManager) hueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        searchManager.search(true, true);
    }

    public void setAccessPoints(List<PHAccessPoint> accessPoints) {
        mAdapter.setItems(accessPoints);
        vConnectManually.setEnabled(true);
    }
}
