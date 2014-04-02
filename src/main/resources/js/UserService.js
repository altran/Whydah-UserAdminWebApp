UseradminApp.service('Users', function($http, Messages){
	
	this.list = [];
	this.user = {};
	this.selected = false;
	this.applications = [];
	this.applicationFilter = [];

	this.search = function(searchQuery) {
		console.log('Searching for users...');
		searchQuery = searchQuery || '*';
		var that = this;
		$http({
			method: 'GET',
			url: baseUrl+'users/find/'+searchQuery
			//url: 'json/users.json',
		}).success(function (data) {
			that.list = data.result;
		});
		return this;
	};

	this.get = function(username, callback) {
	    console.log('Getting user', username);
	    var that = this;
		$http({
			method: 'GET',
			url: baseUrl+'user/'+username+'/'
		}).success(function (data) {
		    console.log('Got user', user);
		    that.user = data;
		    if(callback) {
		        callback(data);
		    }
		});
		return this;
	};

    // Current json-request for save
    // jsond: {"personRef":"1", "username":"leon", "firstName":"Leon", "lastName":"Ho", "email":"leon.ho@altran.com", "cellPhone":"993 97 835"}
	this.save = function(user) {
	    console.log('Saving user', user);
		$http({
			method: 'PUT',
			url: baseUrl+'user/'+user.username+'/',
			data: user
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was saved succesfully.');
		});
		return this;
	};

	this.add = function(user) {
	    console.log('Adding user', user);
		$http({
			method: 'PUT',
			url: baseUrl+'user/',
			data: user
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was added succesfully.');
		});
		return this;
	};

	this.delete = function(user) {
	    console.log('Deleting user', user);
		$http({
			method: 'DELETE',
			url: baseUrl+'user/'+user.username+'/'
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was deleted succesfully.');
		});
		return this;
	};

});