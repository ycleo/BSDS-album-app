package Server;

import Models.ReviewResponse;
import cs6650_assignment.Models.ReviewResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public class RedisClient {
  private final JedisPool jedisPool;
  private final int REDIS_PORT = Integer.parseInt(System.getProperty("redis.port"));

  private final String REDIS_HOST = System.getProperty("redis.host");
  public RedisClient() {
    //"localhost", 6379
    jedisPool = new JedisPool(REDIS_HOST,REDIS_PORT,true);
    jedisPool.setMaxTotal(2000);
    jedisPool.setMaxIdle(2000);
    jedisPool.setMinIdle(2000);
  }
  public void setReview(String id, ReviewResponse res){
    try(Jedis jedis = jedisPool.getResource()){
      Map<String,String> tmp = new HashMap<>();
      tmp.put("likes", res.getLikes());
      tmp.put("dislikes",res.getDislikes());
      Pipeline p=jedis.pipelined();
      p.hmset(id,tmp);
      p.expire(id,90);
      p.sync();
    }
  }

  public ReviewResponse queryReview(String id){
    try (Jedis jedis = jedisPool.getResource()) {
      List<String> res = jedis.hmget(id,"likes","dislikes");
      if(res.get(0)==null){
        return null;
      }
      return new ReviewResponse(res.get(0), res.get(1));
    }
  }

  public void addReview(String id, Boolean likeOrNot){
    try (Jedis jedis = jedisPool.getResource()) {
      String res,target;
      if(likeOrNot){
        target = "likes";
      }else{
        target = "dislikes";
      }
      res = jedis.hget(id,target);
      if(res!=null){
        Pipeline p=jedis.pipelined();
        p.hincrBy(id,target,1);
        p.expire(id,90);
        p.sync();
      }

    }
  }
}
