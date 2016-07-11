PusherSample
============

A basic messaging app built on using the [Pusher API][1] service. 


Curl Usage
----------

```bash
curl --data '{"channel":"Channel1", "name":"my_event", "data":{"userName":"Fred","message":"hello"}}' 
-k -v -i -H "Content-Type: application/json"  https://di.redgringo.com/sendEvent.php
```

[1]: https://www.pusher.com/
