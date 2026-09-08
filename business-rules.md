# Business Rules

## Inputs

* inventory
* onOrderQuantity
* averageDailySales: 
* leadTimeDays
* bufferDays
* orderCoverageDays

## Rules

### - reorderPoint
How many days the current inventory position (including on order quantity) will last based on average daily sales.


`averageDailySales * (leadTimeDays + bufferDays)`

Example:

* Daily sales: 10
* Lead time: 10 days
* Buffer: 5 days

*reorderPoint* is at 150. When current inventory position drops below this point, *shouldReorder* is triggered.

### - shouldReorder
Reorder timing informs merchant to reorder.

`shouldReorder = inventoryPosition <= reorderPoint`


### - targetInventory
How much quantity the inventory will consistently hold based on *orderCoverageDays*.

`reorderPoint + (averageDailySales * orderCoverageDays)`

### - recommendedQuantity
How much quantity merchant should reorder regarding a snapshot.

`targetInventory - inventoryPosition`

## Goal
Avoid stockouts without holding unnecessary inventory.
