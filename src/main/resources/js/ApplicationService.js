UseradminApp.service('Applications', function($http){
	
	this.list = [];
	this.selected = false;

	this.search = function() {
		console.log('Searching for applications...');
		var that = this;
		/*
		$http({
			method: 'GET',
			url: baseUrl + 'applications/find/*'
		}).success(function (data) {
			that.list = data.applications;
		});
		*/
		return this;
	};

});