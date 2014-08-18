UseradminApp.controller('ApplicationCtrl', function($scope, Apps) {

  $scope.session.activeTab = 'application';

  $scope.apps = Apps;

  $scope.searchApps = function() {
  	Apps.search($scope.searchQuery);
  }

  function init() {
    Apps.search();
  }

  if(Apps.list.length<1) {
    init();
  }

});