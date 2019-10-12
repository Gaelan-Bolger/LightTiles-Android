package day.cloudy.apps.tiles.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import day.cloudy.apps.tiles.R;

import static butterknife.ButterKnife.bind;

/**
 * Created by Gaelan Bolger on 1/1/2017.
 * Dialog for requesting an IP address
 */
public class IpRequestDialog extends DialogFragment {

    private Button vPositive;

    public interface Listener {
        void onIpValidated(String ipAddress);
    }

    @BindView(R.id.edit_text_ip_address)
    EditText vEditText;

    private Listener mListener;

    public static IpRequestDialog newInstance(Listener listener) {
        IpRequestDialog dialog = new IpRequestDialog();
        dialog.setListener(listener);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.dailog_ip_request, null);
        bind(this, view);

        vEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != vPositive)
                    vPositive.setEnabled(Patterns.IP_ADDRESS.matcher(s).matches());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(true)
                .setTitle(R.string.bridge_ip_address)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // handled in onShow
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                vPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                vPositive.setEnabled(false);
                vPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ipAddress = vEditText.getText().toString().trim();
                        if (!Patterns.IP_ADDRESS.matcher(ipAddress).matches()) {
                            vEditText.setError(getString(R.string.invalid_ip_address));
                            return;
                        }
                        if (null != mListener)
                            mListener.onIpValidated(ipAddress);
                        dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }
}
