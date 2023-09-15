/*
A message processing component receives an unknown number of messages that are numbered from 0..N, one at a time.
The value of N is unknown. Delivery is reliable in the sense that all messages will eventually be received.
 However, the order in which they are received can be completely random.
 All messages are to be handed off to a Processor with an interface like the following:

process(List<Message> batch)

Any number of batches can be submitted to the processor, however, all messages in a given batch must be in order,
with no gaps. Batches must be in the proper order, with no gaps. For example,
if the first batch of messages sent to the Processor contains messages 0..3, the next batch must start with message 4.
There is no minimum or maximum batch size.

Your task is to complete a collating Listener that receives a single message,
 buffers it in a datastructure (of your choosing) and from time to time submits a valid batch of buffered messages
 to the processor. Here is an example for receiving messages four times and what you need to do every
 time when you receive them.

Received message: [1,4]
Action: Do not call process

Received message: [0] //asume 0 asume not repeated
Action: Call process([0, 1])

Received message: [2]
Action: Call process([2])

Received message: [5,6,7,3]
Action: Call process([3,4,5,6,7])
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class MessageListener {
    List<Message> batch = new ArrayList<>();
    int counter = 0;
    private final Processor processor;

    public MessageListener(Processor processor) {
        this.processor = processor;
    }

    public void messageReceived(Message message) {
        batch.add(message);
        batch.sort((a, b) -> a.sequence - b.sequence);
        ListIterator<Message> it = batch.listIterator();
        List<Message> processed = new ArrayList<>();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.sequence == counter) {
                processed.add(m);
                counter++;
                it.remove();
            }

        }
        if (!processed.isEmpty()) {
            System.out.print("Action: Call process([");
            processed.forEach(message1 -> System.out.print(message1.sequence));
            System.out.println("])");
        }
    }

}

interface Processor {
    void process(List<Message> batch);
}

class Message {
    public int sequence;
    public String content;

    public Message(int _sequence, String _content) {
        this.sequence = _sequence;
        this.content = _content;
    }
}

/* Testing */

class PrintingProcessor implements Processor {
    public PrintingProcessor() {
    }

    public void process(List<Message> batch) {
        for (Message m : batch) {
            System.out.print(m.content);
        }
        System.out.print("\n");
    }
}

class PrintSequenceBatch {
    public static void main(String[] args) {
        MessageListener messageListener = new MessageListener(new PrintingProcessor());

        Integer[] test1 = new Integer[]{1, 4, 0, 2, 5, 6, 7, 3};
        for (Integer i : test1) {
            messageListener.messageReceived(new Message(i, i.toString()));
        }

    }
}