package in.ebhoot.android.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Product implements Parcelable {
    int Id;

    private String imageUrl;
    private String categoryName;
    private String productName;
    private String price;
    private final String regularPrice;

    public Product(int Id,String imageUrl, String categoryName, String productName, String price,String regularPrice ) {
        this.Id = Id;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.productName = productName;
        this.price = price;
        this.regularPrice = regularPrice;
    }

    protected Product(Parcel in) {
        Id = in.readInt();
        imageUrl = in.readString();
        categoryName = in.readString();
        productName = in.readString();
        price = in.readString();
        regularPrice = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() {
        return Id;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(imageUrl);
        dest.writeString(categoryName);
        dest.writeString(productName);
        dest.writeString(price);
        dest.writeString(regularPrice);
    }
}
