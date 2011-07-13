#	The maximum size of the system volume.
#	You cannot change the size or name once a database has been created.
#	You can add additional space with the addvol utility. A guideline for
#	setting the size of sysvol is:
#		guideline = min_size+(1.25*(avg_o_size+40)*num_objects)
#	where avg_o_size is the average size of objects in the database and
#	      num_objects is the number of objects.
#	For the default extent_size (2), the minimum volume size is 1800K and
#	the maximum size is 2047M. The absolute maximum is platform dependent.

sysvol			1024M system

#	The size of the physical log volume.
#	The volume will be expanded if necessary. The minimum size is 256K.
#	A guideline for setting the size is:
#		guideline = 2*(max_o_size+1K)+32K
#	where max_o_size is the size of the largest object in the database.

plogvol			512M physical.log

#	The size of the logical log volume.
#	The volume will be expanded if necessary. The minimum size is 2M.
#	A guideline for setting the size is:
#		guideline = 32K+(2*max_o+100)*num_o_per_tr*num_tr
#	where max_o is the size of the largest object in the database
#	      num_o_per_tr is the largest number of objects that
#	          will be involved in a transaction
#	      num_tr is the maximum number of concurrent transactions.

llogvol			512M logical.log

#	The number of pages per extent on the system volume. An extent is
#	the minimum unit of allocation to segments. In general, setting a
#	low number of pages per extent causes less space to be waster by
#	fragmentation and decreases the initial size of an empty database.
#	It also makes it more likely that, over time, objects on the same
#	segment will not be as close to each other.
#
#	The default is set to 2 and is appropriate for most applications.

extent_size			2

#	Specify whether logical and physical logging are enabled.
#	Logging must be turned on to perform transaction rollbacks or
#	savepoints. Turning logging off is generally not recommended
#	for transactions doing any kind of write operation. Using a
#	raw device for logging could improve performance.

logging			on

#	Specify whether short locking is enabled.
#	Databases that have no concurrent access (most likely personal
#	databases) may have locking turned OFF for better performance.
#	If multiple applications are accessing a database, locking should
#	always be on to ensure orderly concurrent access to objects.

locking			on

#	Specify whether server process buffers are flushed to disk
#	after commits.

commit_flush			off

#	Specify whether polling process should use group read
#	optimization during resynchronization.

polling_optimize			off

#	Specify whether to use the server thread or a separate thread to flush
#	cache. If set to greater to zero, a separate thread will be used to
#	flush the server cache to disk. If set to zero, the server process
#	itself will flush the server cache.

async_buffer_cleaner			1

#	Specify whether to use the server thread or a separate thread to flush
#	logging cache. If set to greater than zero, a separate thread will be
#	used to flush the logging cache to disk. If set to zero, the server
#	process itself will flush the logging cache.

async_logger			1

#	The event registration mode. 
#	The default mode is transient. You can also set it to persistent
#	depending upon the situation. However, you should understand the
#	difference between each mode before the change.

event_registration_mode			old_transient

#	The event message mode.
#	The default mode is transient. You can turn on persistent mode 
#	by setting its value to persistent

event_msg_mode			transient

#	The transient event message queue size
#	The default size is 20480. You can set it to any value 
#	in between of 0 and 2^32, noninclusive.

event_msg_transient_queue_size			20480

#	Specify when the cleaner thread starts flushing dirty pages.
#	Together, the high and low water marks influence when the cleaner
#	writes dirty pages to disk. Without the lower water mark concept,
#	pages that are repeatedly set dirty would otherwise get flushed over
#	and over again. Without there being a gap between the high and low
#	water marks, a page that is repeatedly set dirty might go for a long
#	time without being flushed.

bf_dirty_high_water_mark			131072

#	Specify when the cleanup thread stops flushing dirty pages.

bf_dirty_low_water_mark			204

#	Number of cached user defined class.
#	If more than the specified number of classes are accessed during the
#	time a database is running, the cache gets resized automatically.
#	For optimal performance you should set this parameter equal to or
#	higher than the number of cached user defined classes.

class			480

#	Specify database timeout.
#	The database will be automatically shutdown if there have been no
#	active transactions for the specified number of minutes. The
#	default value is -1, which means no timeout. All negative values
#	also mean no timeout.

db_timeout			-1

#	Number of cached user defined indexes.
#	If more than the specified number of indexes are accessed during the
#	time a database is running, the cache gets resized automatically.
#	For optimal performance you should set this parameter equal to or
#	higher than the number of cached user defined indexes.

index			480

#	Logical log buffer size in bytes.
#	Decreasing this will save some memory but increase the frequency of
#	disk writes. Increasing this will slightly reduce the frequency of
#	disk writes but may waste memory and cause paging.

llog_buf_size			256M

#	Period of time to wait for placing a lock on an object before giving
#	up. This parameter also affects the breaking of a soft persistent
#	lock with a hard persistent lock. The breakage is delayed by an amount
#	of time equal to this parameter. Use the value -1 to specify waiting
#	forever and use 0 to specify immediate return (non-blocking). If a
#	lock cannot be acquired in the specified number of seconds, the
#	following error message will be returned:
#		2903 SM_LOCK_TIMEOUT		Lock wait timed out

lock_wait_timeout			60

#	Maximum number of 16K buffers for caching data pages.
#	This parameter strongly influences system performance. If too low,
#	disk input/output will increase. If too high, physical memory will be
#	exhausted and virtual memory swapping to the disk will take place
#	thus defeating the purpose of caching disk pages. For a large
#	database, you may be able to increase performance by increasing this.
#	For small databases, you may want to decrease it to save memory.

max_page_buffs			131072

#	Set database latching behavior.
#	For a group database, setting it to on will improve concurrency on a
#	multiple processor machine. For a personal database, setting it to
#	off will improve performance when it is accessed by a single user.

multi_latch			on

#	Size of the physical logging buffer.
#	Decreasing this will save some memory but increase the frequency of
#	disk writes. Increasing this will slightly reduce the frequency of
#	disk writes but may waste memory and cause paging.

plog_buf_size			256M

#	A pre-allocation hint for the initial size of server heap.

heap_size			1G

#	The initial size for each allocated heap arena.

heap_arena_size			50M

#	Set the size by which an arena will be expanded as needed.

heap_arena_size_increment			10M

#	The maximum amount of unused top-most memory to keep
#	before releasing to the memory allocator.

heap_arena_trim_threshold			10M

#	The maximum number of heap arenas.

heap_max_arenas			-1

#	If off, suppresses traversals of memory segments
#	returned by the memory allocator. This disables
#	merging of segments that are continuous, and selectively
#	releasing them to the memory allocator if unused,
#	but bounds execution times.

heap_arena_segment_merging			off

#	Maximum number of concurrent transactions.

transaction			1000

#	Number of cached user names.
#	If more than the specified number of user names are used during the
#	time a database is running, you will get an error.

user			8

#	Number of cached data volumes.
#	If more than the specified number of data volumes are used during the
#	time a database is running, you will get an error.

volume			16

#	Statistic Parameter

#	Turn collection of connection and database statistics on or off.
#	If set to on, statistics will be collected each time an application
#	connects to this database and continue unless explicitly stopped
#	with vstats or o_collectstats().

stat			all off
#	Assertion checking can be set to levels between 0 and 4.
#	The higher the level, the more expensive the checking.
#	The default is set to level 0 where only simple assertions
#	will be performed.

assertion_level			0

#	Number of entries to be maintained in the trace file.

trace_entries			10000

#	Name of the trace file.

trace_file			.systrace

#	DBA logging level: 1 or 0 for now. Default: 1.

versant_be_dbalogginglevel			1

#	syslog logging level: 1 or 0 for now. Default: 1.

be_syslog_level			1

#	Blackbox trace components.

blackbox_trace_comps			off

#	Specify if arrays/vstr of o_1b/o_u1b are treated as
#	strings within a query.

treat_vstr_of_1b_as_string_in_query			off

