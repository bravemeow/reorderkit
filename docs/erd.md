```mermaid
erDiagram
    Store ||--o{ ProductVariant : "1-N"
    Store ||--o{ NotificationHistory : "1-N"
    ProductVariant ||--o{ ReorderHistory : "1-N"
    
    Store {
        string storeId PK
        string storeName
        string OAuth
    }
    
    ProductVariant {
        string variantId PK
    }
    
    ReorderHistory {
        int id PK
    }
    
    NotificationHistory {
        int id PK
    }
```