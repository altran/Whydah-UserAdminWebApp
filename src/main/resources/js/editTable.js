UseradminApp.directive('editTable', function() {
  return {
    restrict: 'A',
    replace: true,
    scope: { elements: '=', visibleFields: '=', elementsSelected: '=', editAction: '&' },
    templateUrl: 'template/editTable.html',
    controller: function($scope, $element) {
    }
  };
});