
/*global Ext, Application */

Ext.BLANK_IMAGE_URL = './ext3/resources/images/default/s.gif';

Ext.ns('Application');


// application main entry point
Ext.onReady(function() {

    Ext.QuickTips.init();
    Bus = new Ext.util.Observable();
    Bus.addEvents('message');


	// This just creates a window
    var win = new Ext.Window({
         title: appTitle,
         width:820,
         height:850,
         minWidth:500,
         minHeight:280,
         y:10,
         x:10,
         plain:true,
         layout:'fit',
         border:false,
         closable:false,
         items:{xtype:'frontpage'}
    });
    win.show();
   

}); // eo function onReady

// eof