package com.example.pokusaj.Interface;

import com.example.pokusaj.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {

void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);
}
