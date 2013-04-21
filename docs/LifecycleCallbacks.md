Entity Lifecycle Callback Methods
=================================

A kind of GORM-like domain entity lifecycle callback methods are supported.
This methods are auto-magically called (of course, only if they are implemented by domain entities) 
when predefined events are raised.

Supported events and methods are:
* <code>beforeInsert()</code>
* <code>afterInsert()</code>
* <code>beforeUpdate()</code>
* <code>afterUpdate()</code>
* <code>beforeDelete()</code>
* <code>afterDelete()</code>
* <code>onLoad()</code>. Called inmediatelly after entity is loaded from database.