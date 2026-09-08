# Business Rules

## Inputs

* Current inventory
* Average daily sales
* 30/60/90 day sales trend
* Lead time
* Reorder cycle
* Buffer days

## Reorder Timing

Estimate how many days the current inventory will last based on average daily sales.

Recommend reorder when inventory coverage reaches:

`lead time + buffer days`

Example:

* Inventory: 120
* Daily sales: 10
* Lead time: 10 days
* Buffer: 5 days

Inventory lasts about 12 days. Since lead time + buffer is 15 days, reorder now.

## Reorder Quantity

Recommend enough units to cover:

`daily sales * (reorder cycle + buffer days) - current inventory`

Example:

* Inventory: 120
* Daily sales: 10
* Reorder cycle: 30 days
* Buffer: 5 days

Recommended reorder quantity: about 230 units.

## Goal

Avoid stockouts without holding unnecessary inventory.
