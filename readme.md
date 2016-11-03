Basic event lib todo:

Fix issue with applications using the THREAD_POOL method not ever terminating. I assume it has something to do with the delegation thread never terminating. I'll have to think of something to solve that problem.

There's also a problem with the NON_BLOCKING_LINEAR notify method that causes an exception first, then it seems to work afterwards. It may be that these two issues are related.
