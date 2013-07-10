UserAdministration
========================

Administration UI for Whydah Users and their mapping to Roles, Applications and Organizations.
Requires UserIdentityBackend, and if authorization is turned on; SSOLoginService and SecurityTokenService.
In order to use the Administration UI the User requires a UserAdmin-role defined in UserIdentityBackend.

TODO: 
Show proper error message when receiving connection errors. Connection Refused leaves a page without any data.
Change localization to English.
What does myJsonPersonCustomerStore.js do? It tries to get "members" from url: myHostJson+'?url=http://localhost:9999/members/'. Remove this?
Do a comprehensive test of the functionality in the UI. Does everything work as expected? If not, do we need it?