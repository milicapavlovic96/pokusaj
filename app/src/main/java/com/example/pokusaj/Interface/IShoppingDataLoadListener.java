package com.example.pokusaj.Interface;

import com.example.pokusaj.Model.ShoppingItem;

import java.util.List;

public interface IShoppingDataLoadListener {
void onShoppingLoadSuccess(List<ShoppingItem> shoppingItemList);
void onShoppingDataLoadFailed(String message);
}
