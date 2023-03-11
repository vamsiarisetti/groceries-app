package com.org.groceries.repository;

import com.org.groceries.entity.GroceryList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroceryRepository extends JpaRepository<GroceryList, Long>
{
	List<GroceryList> findByOrderDate(LocalDate orderDate);

	GroceryList findByProductName(String productName);

	@Query(nativeQuery = true, value = "UPDATE grocery_list SET product_quantity=:productQuantity WHERE product_name=:productName")
	GroceryList updateQuantity(String productQuantity, String productName);

	@Query(nativeQuery = true, value = "SELECT * FROM grocery_list WHERE user_id IN ('common', :userId)")
	List<GroceryList> findByUserId(String userId);

	@Query(nativeQuery = true, value = "SELECT DISTINCT user_id FROM grocery_list")
	List<String> getDistinctUserIds();
}
