package in.ebhoot.android.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Category implements Parcelable {
    private int id;
    private String name;
    private int count;
    private int parentId;
    private List<Category> subcategories;

    public Category(int id, String name, int count, int parentId) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.parentId = parentId;
    }

    private Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        count = in.readInt();
        parentId = in.readInt();
        subcategories = new ArrayList<>();
        in.readList(subcategories, Category.class.getClassLoader());
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public int getParentId() {
        return parentId;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(count);
        dest.writeInt(parentId);
        dest.writeList(subcategories);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
