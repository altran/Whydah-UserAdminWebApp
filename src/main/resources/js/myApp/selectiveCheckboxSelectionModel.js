// Create the namespace
Ext.ns('Ext.ux.grid');

/**
 * Ext.ux.grid.selectiveCheckboxSelectionModel Selective CheckboxSelectionModel
 *
 * An extension to the CheckboxSelectionModel that lets you specify a function
 * that determines whether a checkbox should be displayed for each column.
 *
 * Initial Release Date: March 24, 2009
 *
 * Changes:
 * - 1.0:  Initial release v1.0
 * - 1.01: Added beforerowselect handler to prevent selection of non-checkable
 *         rows by RowSelectionModel's selectRow
 * - 1.02: Fixed silly bug (using getElementsByClassName is never good)
 * - 1.03: Changed assignment of renderer to implicit method naming for Ext3
 *         WARNING: Breaks compatibility with Ext2!
 * - 1.04: Simplified hasChecker method (thanks to turbovegas)
 *
 * @author  BitPoet
 * @date    June 10, 2009
 * @version 1.04
 *
 * @class Ext.ux.grid.selectiveCheckboxSelectionModel
 * @extends Ext.grid.CheckboxSelectionModel
 */


/**
 * Constructor
 *
 * @constructor
 * @param {ConfigObject} config
 */
Ext.ux.grid.selectiveCheckboxSelectionModel = function(config) {
	Ext.ux.grid.selectiveCheckboxSelectionModel.superclass.constructor.call(this, config);
};

Ext.extend( Ext.ux.grid.selectiveCheckboxSelectionModel, Ext.grid.CheckboxSelectionModel );
Ext.override( Ext.ux.grid.selectiveCheckboxSelectionModel, {
    // needed to set the scope for the renderer function on initialization
    initEvents: function() {
    	Ext.ux.grid.selectiveCheckboxSelectionModel.superclass.initEvents.call(this);
    	//this.renderer = this.newRenderer.createDelegate(this);
    	this.on('beforerowselect', this.checkSelectable);
    },
    // The new renderer that looks for a checkboxCondition on the grid
    renderer : function(v, p, record, rowIdx, colIdx, store){
    	if( ! this.grid.checkboxCondition || this.grid.checkboxCondition(v, p, record, rowIdx, colIdx, store) ) {
        	return '<div class="x-grid3-row-checker">&#160;</div>';
        }
        return '';
    },
    selectAll : function(){
        if(this.isLocked()) return;
        this.selections.clear();
        var col = this.grid.getColumnModel().getIndexById('checker');
        for(var i = 0, len = this.grid.store.getCount(); i < len; i++){
            if( this.hasChecker(this.grid.getView().getCell(i, col)) ) {
                this.selectRow(i, true);
            }
        }
    },
    checkSelectable: function(sm, rowIdx, keep, rec) {
    	var col = sm.grid.getColumnModel().getIndexById('checker');
        if( this.hasChecker(this.grid.getView().getCell(rowIdx, col)) == true ) {
            return true;
        }
        return false;
    },
    hasChecker: function(node) {
        return Ext.query('div[class*=x-grid3-row-checker]', node).length > 0;
    }
/*
    hasChecker:	function(node) {
    	var hasCheckerNode = false;
    	var subdivs = node.getElementsByTagName('div');
    	for( var i = 0; i < subdivs.length; i++ ) {
    	    if( subdivs[i].className ) {
    	    	if( 'x-grid3-row-checker' == subdivs[i].className )
    	        	hasCheckerNode = true;
    	    }
    	}
    	return hasCheckerNode;
    }
*/
});

// EOF Ext.ux.grid.selectiveCheckboxSelectionModel
