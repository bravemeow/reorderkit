# Shopify Inventory Reorder Assistant

## MVP Requirements

### 1. Target User

Shopify merchants who hold physical inventory and have a periodic reorder cycle.

### 2. Problem

Merchants need to repeatedly decide:

* Which products need to be reordered
* When they should be reordered
* How many units should be reordered

Overstock ties up cash in inventory, while understock increases the risk of stockouts.

### 3. MVP User Flow

1. Merchant opens the app.
2. Merchant configures inventory settings and syncs Shopify inventory data.
3. The app analyzes inventory and recent sales history.
4. Merchant sees which products need to be reordered and the recommended reorder quantity.

### 4. MVP Features

The merchant can configure:

* onOrderQuantity
* Supplier lead time

For each product, the app shows:

* Average daily sales
* Current inventory
* Estimated days until stockout
* Recommended reorder quantity

### 5. Out of Scope

The MVP will not:

* Automatically place reorder requests
* Manage suppliers
* Act as a full inventory management system

The application only provides information and recommendations to support the merchant's reorder decisions.

### 6. Optimization Goal

Overstock ties up cash in inventory, while understock increases the risk of stockouts.

The app should recommend reorder quantity that minimize excess inventory while maintaining enough stock to meet expected demand.
