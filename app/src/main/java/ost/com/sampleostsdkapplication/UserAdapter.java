package ost.com.sampleostsdkapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private List<UserData> mDataset;
    private TextDrawable.IBuilder mBuilder;
    private OnItemSelectedListener mListener;

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mListener = onItemSelectedListener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final TextView mMobile;
        private final ImageView mProfilePic;
        // each data item is just a string in this case
        public TextView mDescription;
        public MyViewHolder(View v) {
            super(v);
            mDescription = v.findViewById(R.id.description);
            mName = v.findViewById(R.id.name);
            mMobile = v.findViewById(R.id.mobile_number);
            mProfilePic = v.findViewById(R.id.profile);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public UserAdapter(List<UserData> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_view_holder, parent, false);

        mBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .rect();

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.mDescription.setText(mDataset.get(position).getDescription());
        holder.mName.setText(mDataset.get(position).getName());
        holder.mMobile.setText(mDataset.get(position).getMobile());
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT

        int color = generator.getColor(mDataset.get(position).getId());
        TextDrawable drawable = mBuilder.build(mDataset.get(position).getName().substring(0,1).toUpperCase(), color);
        holder.mProfilePic.setImageDrawable(drawable);

        View.OnClickListener onClickListener = getOnClickListener(
                mDataset.get(position).getTokenHolderAddress()
        );

        holder.mProfilePic.setOnClickListener(onClickListener);

        holder.mName.setOnClickListener(onClickListener);

        holder.mMobile.setOnClickListener(onClickListener);
    }

    private View.OnClickListener getOnClickListener(String tokenHolderAddress) {
        if (null == mListener) {
            return null;
        }
        return v -> mListener.onItemSelected(tokenHolderAddress);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String tokenHolderAddress);
    }
}