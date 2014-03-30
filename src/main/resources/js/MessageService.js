UseradminApp.service('Messages', function(){
	this.list = [];
	this.add = function(type, text) {
        this.list.push({
            type: type,
            text: text
        });
	};
	this.remove = function(index) {
        this.list.splice(index,1);
	};
});