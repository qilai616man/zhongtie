package com.digital.dl2.business.core.obj;

import com.digital.dl2.business.net.obj.NetShoppingCart;
import com.digital.dl2.business.net.obj.NetShoppingCartData;
import com.goyourfly.gdownloader.utils.Ln;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by digital.dl2 on 15/9/22.
 */
public class PgShoppingCart {
    private int entityId;//购物车ID
    private int type;//图书类型（1：实体书，2：电子书）
    private int number;//图书数量
    private float allPrice;//总价格
    private float preferentialPrice;//优惠
    private List<PgShoppingCartItem> bookItem;

    public static List<PgShoppingCart> getPgListByNet(NetShoppingCart net) {
        List<PgShoppingCart> list = new ArrayList<>();
        PgShoppingCart realShopping = new PgShoppingCart();
        PgShoppingCart eShopping = new PgShoppingCart();

        NetShoppingCartData netRealBookCart = net.getData().getReal_book();
        NetShoppingCartData netEBookCart = net.getData().getE_book();

        if(netRealBookCart != null){
            Ln.d("PgShoppingCart:netRealBookCart:" + netRealBookCart.toString());
            List<PgShoppingCartItem> realBook = PgShoppingCartItem.getPgListByNetList(netRealBookCart.getBook_list());

            realShopping.setType(netRealBookCart.getType());
            realShopping.setNumber(netRealBookCart.getNumber());
            realShopping.setAllPrice(netRealBookCart.getAll_price());
            realShopping.setPreferentialPrice(netRealBookCart.getPreferential_price());
            realShopping.setBookItem(realBook);

            list.add(realShopping);
        }

        if(netEBookCart != null){
            Ln.d("PgShoppingCart:netEBookCart:" + netEBookCart.toString());
            List<PgShoppingCartItem> eBook = PgShoppingCartItem.getPgListByNetList(netEBookCart.getBook_list());

            eShopping.setType(netEBookCart.getType());
            eShopping.setNumber(netEBookCart.getNumber());
            eShopping.setAllPrice(netEBookCart.getAll_price());
            eShopping.setPreferentialPrice(netEBookCart.getPreferential_price());
            eShopping.setBookItem(eBook);

            list.add(eShopping);
        }

        return list;
    }

    @Override
    public String toString() {
        return "PgShoppingCart{" +
                "entityId=" + entityId +
                ", type=" + type +
                ", number=" + number +
                ", allPrice=" + allPrice +
                ", preferentialPrice=" + preferentialPrice +
                ", bookItem=" + bookItem +
                '}';
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(float allPrice) {
        this.allPrice = allPrice;
    }

    public float getPreferentialPrice() {
        return preferentialPrice;
    }

    public void setPreferentialPrice(float preferentialPrice) {
        this.preferentialPrice = preferentialPrice;
    }

    public List<PgShoppingCartItem> getBookItem() {
        return bookItem;
    }

    public void setBookItem(List<PgShoppingCartItem> bookItem) {
        this.bookItem = bookItem;
    }
}
