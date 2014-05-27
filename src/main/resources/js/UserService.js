UseradminApp.service('Users', function($http, Messages){
	
	this.list = [];
	this.user = {};
	this.userRoles = {};
	this.searchQuery = '*';
	this.selected = false;
	this.applications = [];
	this.applicationFilter = [];

	this.search = function(searchQuery) {
		console.log('Searching for users...');
		this.searchQuery = searchQuery || '*';
		var that = this;
		$http({
			method: 'GET',
			url: baseUrl+'users/find/'+this.searchQuery
			//url: 'json/users.json',
		}).success(function (data) {
			that.list = data.result;
		});
		return this;
	};

	this.get = function(uid, callback) {
	    console.log('Getting user', uid);
	    var that = this;
		$http({
			method: 'GET',
			url: baseUrl+'user/'+uid+'/'
		}).success(function (data) {
		    console.log('Got user', data);
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
	    var that = this;
		$http({
			method: 'PUT',
			url: baseUrl+'user/'+user.uid+'/',
			data: user
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was saved successfully.');
		    that.search();
		});
		return this;
	};

	this.add = function(user) {
	    console.log('Adding user', user);
	    var that = this;
		$http({
			method: 'POST',
			url: baseUrl+'user/',
			data: user
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was added successfully.');
			that.search();
		});
		return this;
	};

	this.delete = function(user) {
	    console.log('Deleting user', user);
	    var that = this;
		$http({
			method: 'DELETE',
			url: baseUrl+'user/'+user.uid+'/'
		}).success(function (data) {
			Messages.add('success', 'User "'+user.username+'" was deleted successfully.');
			that.search();
		});
		return this;
	};

    this.getRolesForCurrentUser = function(callback) {
        var uid = this.user.uid;
	    console.log('Getting roles for user', uid);
	    var that = this;
		$http({
			method: 'GET',
			url: baseUrl+'user/'+uid+'/roles/'
		}).success(function (data) {
		    console.log('Got userroles', data);
		    that.userRoles = data;
		    if(callback) {
		        callback(data);
		    }
		});
		return this;
    }

    this.addRoleForUser = function(role, user) {
	    console.log('Adding role for user', user, role);
	    var that = this;
		$http({
			method: 'POST',
			url: baseUrl+'user/'+user.uid+'/role/',
			data: role
		}).success(function (data) {
			Messages.add('success', 'Role for user "'+user.username+'" was added successfully.');
			that.getRolesForCurrentUser();
			that.search();
		});
		return this;
    }

	this.addRoleForCurrentUser = function(role) {
	    this.addRoleForUser(role, this.user);
	}

    this.deleteRoleForUser = function(role, user) {
	    console.log('Deleting role for user', user, role);
	    var that = this;
	    var roleName = role.applicationRoleName;
		$http({
			method: 'DELETE',
			url: baseUrl+'user/'+user.uid+'/role/'+role.roleId,
			data: role
		}).success(function (data) {
			Messages.add('success', 'Role "'+roleName+'" for user "'+user.username+'" was deleted successfully.');
			that.getRolesForCurrentUser();
			that.search();
		}).error(function (data) {
            Messages.add('warning', 'Role "'+roleName+'" for user "'+that.user.username+'" was not deleted.');
            that.getRolesForCurrentUser();
        });
		return this;
    }

	this.deleteRoleForCurrentUser = function(role) {
	    this.deleteRoleForUser(role, this.user);
	}

    this.saveRoleForUser = function(role, user) {
	    console.log('Saving role for user', user, role);
	    var that = this;
	    var roleName = role.applicationRoleName;
		$http({
			method: 'PUT',
			url: baseUrl+'user/'+user.uid+'/role/'+role.roleId,
			data: role
		}).success(function (data) {
			Messages.add('success', 'Role "'+roleName+'" for user "'+that.user.username+'" was saved successfully.');
			that.getRolesForCurrentUser();
			that.search();
		}).error(function (data) {
			Messages.add('warning', 'Role "'+roleName+'" for user "'+that.user.username+'" was not saved.');
			that.getRolesForCurrentUser();
		});
		return this;
    }

    this.saveRoleForCurrentUser = function(role) {
        this.saveRoleForUser(role, this.user);
    }

});