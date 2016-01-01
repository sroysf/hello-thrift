namespace java com.force.thrift

// ========== Transaction log events =========

struct FoundEdgeMartEvent {
  1: string emId,
  2: i64 lastAccessTime,
}

struct ProducedEdgeMartEvent {
  1: string emId,
}

struct CoreDeleteEdgeMartEvent {
  1: string emId,
}

struct DownloadedEdgeMartEvent {
  1: string emId,
  2: i64 lastAccessTime,
}

struct AccessTimeUpdatedEdgeMartEvent {
  1: string emId,
  2: i64 lastAccessTime,
}

// ========== Transaction log RPC request/response types =========

service EdgeControlAPI {
    TransactionList playbackTransactions(1:i32 start, 2:i32 maxTransactions),
}

struct MessageEnvelope {
  1: i16 typeId,
  2: binary rawMsgBytes,
}

struct TransactionList {
  1: string uniqueLogId,
  2: list<MessageEnvelope> envelopes,
}

struct TrannsactionId {
  1: string uniqueLogId,
  2: i32 transactionId,
}