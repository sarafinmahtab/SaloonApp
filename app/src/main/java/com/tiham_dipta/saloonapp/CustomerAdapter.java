package com.tiham_dipta.saloonapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tiham_dipta.saloonapp.models.Customer;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_single_customer, viewGroup, false);

        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder customerViewHolder, int i) {
        final Customer customer = customerList.get(i);

        customerViewHolder.userNameTextView.setText(customer.getCustomerName());
        customerViewHolder.scheduleTextView.setText(customer.getScheduleTime());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {

        private TextView userNameTextView;
        private TextView scheduleTextView;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTextView = itemView.findViewById(R.id.name_text);
            scheduleTextView = itemView.findViewById(R.id.time_text);
        }
    }
}
