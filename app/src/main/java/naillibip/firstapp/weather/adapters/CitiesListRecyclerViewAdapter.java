package naillibip.firstapp.weather.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.andexert.library.RippleView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import butterknife.ButterKnife;
import butterknife.BindView;
import naillibip.firstapp.weather.CityCard;
import naillibip.firstapp.weather.callBackInterfaces.IAdapterCallbacks;
import naillibip.firstapp.weather.R;

public class CitiesListRecyclerViewAdapter extends RecyclerView.Adapter<CitiesListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<CityCard> data;
    private IAdapterCallbacks adapterCallbacks;

    public CitiesListRecyclerViewAdapter(ArrayList<CityCard> data, IAdapterCallbacks adapterCallbacks) {
        Log.d("CitiesListRVAdapter", "creating new adapter with " + data.toString());
        this.data = data;
        this.adapterCallbacks = adapterCallbacks;
    }

    public void addItem(CityCard cityCard) {
        data.add(0, cityCard);
        notifyItemInserted(0);
    }

    public void removeItem() {
        Log.d("CitiesListRVAdapter", "removingItem");
        if (!data.isEmpty()) {
            data.remove(0);
            notifyItemRemoved(0);
        }
    }

    public void swapItems(int from, int to) {
        Collections.swap(data, from, to);
        notifyItemMoved(from, to);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.cities_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.imageButton.setOnClickListener(view -> Snackbar.make(view, "Delete this city?", Snackbar.LENGTH_LONG)
                .setAction("Yes", view12 -> {
                    if (!data.isEmpty()) {
                        String city = holder.cityName.getText().toString();
                        Log.d("CitiesListRVAdapter", "deleting item " + city + " " + data.toString());
                        CityCard cityCard = new CityCard(city);
                        int pos = data.indexOf(cityCard);
                        data.remove(cityCard);
                        notifyItemRemoved(pos);
                        adapterCallbacks.saveList();
                    }
                }).show());

        holder.temp.setText(
                String.valueOf(
                        Math.round(data.get(position).getTemp())
                ).concat("°"));

        holder.feels_temp.setText(
                String.valueOf(
                        Math.round(data.get(position).getFeelsTemp())
                ).concat("°"));

        holder.icon.setText(String.valueOf(data.get(position).getIcon()));
        holder.cityName.setText(data.get(position).getCityName());
        holder.rippleView.setOnClickListener(view -> adapterCallbacks.startTempScreenFragment(holder.cityName.getText().toString()));
        adapterCallbacks.onAdapterUpdate();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cityTextViewOnCard)
        TextView cityName;
        @BindView(R.id.icon_card)
        TextView icon;
        @BindView(R.id.temp_card)
        TextView temp;
        @BindView(R.id.feels_like_temp_card)
        TextView feels_temp;
        @BindView(R.id.delete_city_btn)
        ImageButton imageButton;
        @BindView(R.id.rippleView)
        RippleView rippleView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            Typeface weatherFont = Typeface.createFromAsset(Objects.requireNonNull(view.getContext()).getAssets(), "fonts/weathericons.ttf");
            icon.setTypeface(weatherFont);
        }
    }
}
