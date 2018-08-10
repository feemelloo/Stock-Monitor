package com.stockmonitor.stockmonitor;

import android.os.Parcel;
import android.os.Parcelable;

public class Stock implements Parcelable {
    private String symbol;
    private double price;
    private double high;
    private double low;
    private double open;
    private double volume;
    private String currency;

    public Stock(String symbol, double price, double high, double low, double open, double volume, String currency) {
        this.symbol= symbol;
        this.price = price;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
        this.currency = currency;
    }

    private Stock(Parcel in) {
        symbol = in.readString();
        price = in.readDouble();
        high = in.readDouble();
        low = in.readDouble();
        open = in.readDouble();
        volume = in.readDouble();
        currency = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(symbol);
        parcel.writeDouble(price);
        parcel.writeDouble(high);
        parcel.writeDouble(low);
        parcel.writeDouble(open);
        parcel.writeDouble(volume);
        parcel.writeString(currency);
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
