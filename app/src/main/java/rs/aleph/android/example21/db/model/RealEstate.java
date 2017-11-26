package rs.aleph.android.example21.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by KaraklicDM on 25.11.2017.
 */

@DatabaseTable(tableName = RealEstate.TABLE_NAME_REAL_ESTATE)
public class RealEstate {
    public static final String TABLE_NAME_REAL_ESTATE = "realEstate";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_DESCRIPTION = "description";
    public static final String FIELD_NAME_IMAGE = "image";
    public static final String FIELD_NAME_ADRESS = "adress";
    public static final String FIELD_NAME_TELEPHONE = "telephone";
    public static final String FIELD_NAME_QUADRATURE= "quadrature";
    public static final String FIELD_NAME_ROOM = "room";
    public static final String FIELD_NAME_PRICE = "price";




    @DatabaseField(columnName = FIELD_NAME_ID,generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;


    @DatabaseField(columnName = FIELD_NAME_DESCRIPTION)
    private String mDescription;


    @DatabaseField(columnName = FIELD_NAME_IMAGE)
    private String mImage;

    @DatabaseField(columnName = FIELD_NAME_ADRESS)
    private String mAdress;

    @DatabaseField(columnName = FIELD_NAME_TELEPHONE)
    private int mTel;

    @DatabaseField(columnName = FIELD_NAME_QUADRATURE)
    private double mQuadrature;

    @DatabaseField(columnName = FIELD_NAME_ROOM)
    private int mRoom;
    @DatabaseField(columnName = FIELD_NAME_PRICE)
    private double mPrice;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmAdress() {
        return mAdress;
    }

    public void setmAdress(String mAdress) {
        this.mAdress = mAdress;
    }

    public int getmTel() {
        return mTel;
    }

    public void setmTel(int mTel) {
        this.mTel = mTel;
    }

    public double getmQuadrature() {
        return mQuadrature;
    }

    public void setmQuadrature(double mQuadrature) {
        this.mQuadrature = mQuadrature;
    }

    public int getmRoom() {
        return mRoom;
    }

    public void setmRoom(int mRoom) {
        this.mRoom = mRoom;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }


    public RealEstate() {
    }

    @Override
    public String toString() {
        return mName;
    }


}
