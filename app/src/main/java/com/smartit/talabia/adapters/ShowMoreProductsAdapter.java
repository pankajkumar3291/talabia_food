package com.smartit.talabia.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.QuantityField;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 12/4/19.
 */

public class ShowMoreProductsAdapter extends RecyclerView.Adapter <ShowMoreProductsAdapter.ViewHolder> {

    private Context context;
    private ArrayList <Integer> productsDataList;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;

    public ShowMoreProductsAdapter(Context context, ArrayList <Integer> productsDataList) {
        this.context = context;
        this.productsDataList = productsDataList;

        this.languagePreferences = ApplicationHelper.application ( ).languagePreferences ( LANGUAGE_PREFERENCE );
        this.languageId = languagePreferences.getString ( SELECTED_LANG_ID, "" );
        setHasStableIds ( true );
    }

    @NonNull
    @Override
    public ShowMoreProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_more_product_page, parent, false );
        return new ShowMoreProductsAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(final @NonNull ShowMoreProductsAdapter.ViewHolder holder, final int position) {
//        final EOAllProductsPayloadData payloadData = this.productsDataList.get ( position );
//
//        holder.tv_product_name.setText ( payloadData.getProductName ( ) );
//        loadImages ( payloadData.getFeaturedImage ( ), holder.productImage );
//
//        ArrayList <String> unitList = new ArrayList <> ( );
//        if(!ObjectUtil.isEmpty ( payloadData.getPrices ( ) )) {
//            for (EOPrice eoPrice : payloadData.getPrices ( )) {
//                unitList.add ( eoPrice.getUnit ( ) );
//            }
//        }
//
//        ArrayAdapter <String> arrayAdapter = new ArrayAdapter <> ( context, R.layout.spinner_item, unitList );
//        arrayAdapter.setDropDownViewResource ( R.layout.spinner_item );
//        holder.spinner_quantity.setAdapter ( arrayAdapter );
//
//        holder.spinner_quantity.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener ( ) {
//            @Override
//            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
//                if(ObjectUtil.isEmpty ( payloadData.getPrices ( ) ) && !ObjectUtil.isEmpty ( languageId )) {
//                    holder.tv_product_price.setText ( CURRENCY_CODE.concat ( " " ).concat ( "00." ) );
//                    holder.tv_price_after_decimal.setText ( "0" );
//                } else {
//                    for (EOPrice eoPrice : payloadData.getPrices ( )) {
//                        if(eoPrice.getUnit ( ).equalsIgnoreCase ( (String) parent.getItemAtPosition ( position ) )) {
//                            String[] priceArray = String.valueOf ( eoPrice.getRegularPrice ( ) ).split ( "\\." );
//                            if(!ObjectUtil.isEmpty ( priceArray[ 0 ] ) && !ObjectUtil.isEmpty ( languageId )) {
//                                if(languageId.equalsIgnoreCase ( ARABIC_LANGUAGE_ID )) {
//                                    holder.tv_product_price.setText ( ".".concat ( priceArray[ 0 ] ).concat ( " " ).concat ( CURRENCY_CODE ) );
//                                } else {
//                                    holder.tv_product_price.setText ( CURRENCY_CODE.concat ( " " ).concat ( priceArray[ 0 ] ).concat ( "." ) );
//                                }
//                            }
//                            if(!ObjectUtil.isEmpty ( priceArray[ 1 ] )) {
//                                holder.tv_price_after_decimal.setText ( priceArray[ 1 ] );
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView <?> parent) {
//
//            }
//        } );

        holder.quantityField.setOnValueChangeListener ( new QuantityField.OnValueChangeListener ( ) {
            @Override
            public void onValueChanged(String value) {

                Toast.makeText ( context, "value = " + value, Toast.LENGTH_SHORT ).show ( );
            }
        } );
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( productsDataList ) ? 0 : productsDataList.size ( );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TalabiaTextView tv_product_name, tv_add_to_cart;
        private Spinner spinner_quantity;
        private QuantityField quantityField;
        private TextView tv_product_price, tv_price_after_decimal;

        private ViewHolder(@NonNull View itemView) {
            super ( itemView );
            productImage = itemView.findViewById ( R.id.productImage );
            tv_product_name = itemView.findViewById ( R.id.tv_product_name );
            tv_product_price = itemView.findViewById ( R.id.tv_product_price );
            tv_price_after_decimal = itemView.findViewById ( R.id.tv_price_after_decimal );
            tv_add_to_cart = itemView.findViewById ( R.id.tv_add_to_cart );
            spinner_quantity = itemView.findViewById ( R.id.spinner_quantity );
            quantityField = itemView.findViewById ( R.id.quantityField );
            quantityField.setMaxLength ( 3 );
        }
    }

}
