# Reorder calculation notes

Variant config:

- PUT `/stores/{shopDomain}/variants/{variantId}/configuration`
- body: leadTimeDays, bufferDays, orderCoverageDays
- replaces all three overrides. Missing field also becomes null
- null = clear override, use Store default on next calculation
- lead time / buffer >= 0. Coverage > 0. All allow null
- response contains saved overrides, not effective Store default values
- no calculation or new history here. Just settings update
- Entity method + dirty checking, same idea as Store config
- columns already exist. No new migration

Latest result:

- GET `/stores/{shopDomain}/variants/{variantId}/reorder/latest`
- read last history. No new calculation / no history insert
- 200: four result fields + calculatedAt
- 204: variant exists but no history yet. Empty body
- 404: variant missing or belongs to other store
- config changed after calculation? Still show saved result with original time
- LatestReorderResponse is a Java record. Just response data, no setters

POST `/stores/{shopDomain}/variants/{variantId}/reorder/check`

No request body. variantId = our DB id, not Shopify id.

Flow:

- find variant with id + shopDomain. Must belong to that store
- get Store from variant.getStore()
- check Store config is done. avg daily sales must be > 0 and finite
- config: use variant override if not null. Otherwise Store default
- each field checked separately. 0 override is valid for lead time / buffer
- use inventory, onOrder, avg sales from DB
- pass values to ReorderService, calculate
- save inputs + result in ReorderHistory
- return response. Transaction commits before caller gets result

ProductVariantReorderService handles DB flow.
ReorderService handles calculation. Formula / rounding same as before.
Still using ReorderRequest inside service for now. Later can separate calculator input from HTTP DTO.

Old `/reorder/check` still works. Just calculation from request, no DB / history.
New endpoint uses last saved data. No Shopify API call yet.
Every successful call adds history, even with same values.

Errors:

- 404: variant not found, or belongs to other store
- 409: Store config incomplete, or sales value not usable for current calculation
- these cases stop before saving history

TODO: Shopify sync, scheduled calculation, OAuth.
shopDomain in URL is only for lookup. Does not prove user owns the store.

Tests:

- ProductVariantReorderControllerTest: endpoint + HTTP response
- ProductVariantReorderServiceIntegrationTest: real DB, config fallback, calculation, history
- integration test needs local PostgreSQL running. Test data rolled back
