AUI().add('vgr-calendar-portlet',function(A) {
	var Lang = A.Lang,
		isArray = Lang.isArray,
		isFunction = Lang.isFunction,
		isNull = Lang.isNull,
		isObject = Lang.isObject,
		isString = Lang.isString,
		isUndefined = Lang.isUndefined,
		getClassName = A.ClassNameManager.getClassName,
		concat = function() {
			return Array.prototype.slice.call(arguments).join(SPACE);
		},
		
		CALENDAR_WRAP_NODE = 'calendarWrapNode',
		
		CONTENT_BOX = 'contentBox',
		CONTENT_NODE = 'contentNode',
		
		NAME = 'vgr-calendar-portlet',
		NS = 'vgr-calendar-portlet'
		
	;

	var VgrCalendarPortlet = A.Component.create(
			{
				ATTRS: {
					calendarWrapNode: {
						setter: A.one
					}
				},
				EXTENDS: A.Component,
				NAME: NAME,
				NS: NS,
				prototype: {
					tabs: null,
					initializer: function(config) {
						var instance = this;
						
						//instance.initConsole();
					},
					
					renderUI: function() {
						var instance = this;
						
						//A.log(instance.get(CALENDAR_WRAP_NODE));
						
						instance.get(CALENDAR_WRAP_NODE).plug(A.LoadingMask, { background: '#000' });
					},
	
					bindUI: function() {
						var instance = this;
						
						var calendarWrapNode = instance.get(CALENDAR_WRAP_NODE);
						
						calendarWrapNode.delegate('click', instance._onPagerClick, '.pager a', instance);
					},
					
					syncUI: function() {
						var instance = this;
					},
					
					initConsole: function() {
						var instance = this;
						
					    new A.Console({
					        height: '250px',
					        newestOnTop: true,
					        style: 'block',
					        visible: true,
					        width: '600px'
					    }).render();			
					},
					
					_onPagerClick: function(e) {
						var instance = this;
						
						e.halt();
						
						var currentTarget = e.currentTarget;
						
						var pagerUrl = currentTarget.getAttribute('href');
						
						pagerUrl = pagerUrl.replace('p_p_state=normal', 'p_p_state=exclusive');
						
						var pagerIO = A.io.request(pagerUrl, {
							autoLoad : false,
							method : 'GET'
						});
						
						pagerIO.on('success', instance._onUpdateSuccess, instance);
						
						instance.get(CALENDAR_WRAP_NODE).loadingmask.show();
						
						pagerIO.start();
					},
					
					_onUpdateSuccess: function(e, id, xhr) {
						var instance = this;
						
						var data = xhr.responseText;

						var tempNode = A.Node.create(data);
						
						var calendarWrapNode = instance.get(CALENDAR_WRAP_NODE);
						var calendarWrapNodeId = calendarWrapNode.getAttribute('id');

                        var contentNode = tempNode.one('#' + calendarWrapNodeId);
                        if (contentNode) {
                            var content = contentNode.html();

                            tempNode.remove();

                            calendarWrapNode.html(content);

                        }

                        calendarWrapNode.loadingmask.hide();
                    }
					
					
				}
			}
	);

	A.VgrCalendarPortlet = VgrCalendarPortlet;
		
	},1, {
		requires: [
			'aui-base',
			'aui-io-request',
			'aui-loading-mask',
			'event-delegate',
			'node-event-delegate'
      ]
	}
);