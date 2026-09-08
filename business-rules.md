# Business Rules

## Inputs

* Current inventory
* onOrderQuantity
* Average daily sales
* Lead time
* Buffer days

## Reorder Timing

Estimate how many days the current inventory will last based on average daily sales.

Recommend reorder when inventory coverage reaches:

`lead time + buffer days`

Example:

* Inventory: 100
* Daily sales: 10
* Lead time: 10 days
* Buffer: 5 days

Inventory lasts about 10 days. Since lead time + buffer is 15 days, reorder now.

## Reorder Quantity

Recommend enough units to cover:

`daily sales * (lead time + buffer days) - inventory`

Example:

* Inventory: 100
* Daily sales: 10
* Lead time: 30 days
* Buffer: 5 days

Recommended reorder quantity: about 250 units.

## Goal

Avoid stockouts without holding unnecessary inventory.
