# Session notes

Today: read APIs

- GET /stores/{shopDomain}/configuration
- GET /stores/{shopDomain}/variants/{variantId}/configuration
- GET /stores/{shopDomain}/variants?page=0&size=20
- list sorted by DB id. page starts at 0, size 1..100
- list response: items, page, size, totalElements, totalPages
- empty store = 200 with empty items. Missing store = 404
- variant config returns overrides. null still means use Store default
- Store config can be incomplete. Return null values + configured false
- list uses response DTO. No Store entity / access token in response
- no Shopify refresh or calculation from these GET requests

Next session: demo environment

- sample Store + variants
- decide visitor data isolation / reset behavior
- still need onOrderQuantity update API before interactive demo is complete
- then React UI and deployment
- Shopify OAuth, sync, scheduled email still separate work
