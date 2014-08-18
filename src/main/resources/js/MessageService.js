UseradminApp.service('Messages', function($timeout){
    var messageDuration = 5000; // Duration the message is shown in the list.
    var maxMessageCount = 6;
	this.list = [];
	this.add = function(type, text) {
        if(this.list.length>maxMessageCount){
            this.remove(0);
        }
        this.list.push({
            type: type,
            text: text
        });
        var that = this;
        $timeout(function(){
            that.remove(0);
        }, messageDuration);
	};
	this.remove = function(index) {
        this.list.splice(index,1);
	};
});