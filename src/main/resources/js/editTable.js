UseradminApp.directive('editTable', function() {
  return {
    restrict: 'A',
    replace: true,
    scope: { elements: '=', visibleFields: '=', elementsSelected: '=' },
    templateUrl: 'template/editTable.html',
    controller: function($scope, $element) {
    }
  };
});