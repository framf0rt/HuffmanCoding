import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author dano8407 Daniel Nordberg dnordberg86@gmail.com
 * @author anek0941 Anders Ekendahl anders@ekendahl.one
 */

public class HuffmanCoding {

	private HashMap<Character, Integer> freqMap = new HashMap<>();
	private PriorityQueue<Node> prioQueue = new PriorityQueue<>();
	private HashMap<Character, String> encodedChar = new HashMap<>();
	private ArrayList<Character> charList = new ArrayList<Character>();
	private BitSet compressedMessage = new BitSet();
	private Node root;
	
	public static void main(String[] args){
		HuffmanCoding  hc = new HuffmanCoding();
		hc.readFile();
		hc.writeCompressedFile();
		hc.readCompressedFile();
		hc.writeFile();
	}

	public void readFile() {
		File path = new java.io.File(getClass().getClassLoader().getResource("").getPath()).getParentFile();
		BufferedReader bR = null;
		try {

			bR = new BufferedReader(new FileReader(path + "\\test.txt"));
			String currentLine = null;
			while ((currentLine = bR.readLine()) != null) {
				for (int i = 0; i <= currentLine.length(); i++) {
					char c = 'a';

					if (i == currentLine.length()) {
						c = '\n';
					} else {
						c = currentLine.charAt(i);
					}
					charList.add(c);

					if (!freqMap.containsKey(c)) {
						freqMap.put(c, 1);
					} else {
						freqMap.put(c, (freqMap.get(c) + 1));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bR != null) {
				try {
					bR.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void readCompressedFile() {
		File path = new java.io.File(getClass().getClassLoader().getResource("").getPath()).getParentFile();
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(path+"\\testOut.txt");
			ois = new ObjectInputStream(fis);
			try {
				freqMap= (HashMap<Character, Integer>) ois.readObject();
				compressedMessage= (BitSet) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				ois.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void writeFile() {
		BufferedWriter bw = null;
		FileWriter fw = null;
		ArrayList<String> message = decompressMessage();
		File path = new java.io.File(getClass().getClassLoader().getResource("").getPath()).getParentFile();
		try {
			fw = new FileWriter(path + "\\test2.txt");
			bw = new BufferedWriter(fw);
			for(String s:message){
			
				bw.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void writeCompressedFile() {
		File path = new java.io.File(getClass().getClassLoader().getResource("").getPath()).getParentFile();
		File outFile = new File(path+ "\\testOut.txt");
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		root=createHuffTree();
		root.encodeCharacters("");
		compressMessage();
		try {
			fos = new FileOutputStream(outFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(freqMap);
			oos.writeObject(compressedMessage);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {

			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> decompressMessage(){
		ArrayList<String> message = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		root=createHuffTree();
		root.encodeCharacters("");
		int i = 0;
		while(i<compressedMessage.size()){
			//tar ut roten, och går igenom trädet tills dess att man har hittat ett löv
			Node next = root;
			while(next.left!=null&&next.right!=null){
				if(compressedMessage.get(i)){
					next=next.right;
				}else{
					next = next.left;
				}
				i++;
			}
			if(next.character =='\n'){
				sb.append(System.getProperty("line.separator"));
				message.add(sb.toString());
				sb = new StringBuilder();
			}else{
				//lägger till tecknet på nuvarande nod
				sb.append(next.character);
			}
		}
		return message;	
	}
	
	private void createNodeQueue() {
		for (Entry<Character, Integer> entry : freqMap.entrySet()) {
			prioQueue.add(new Node(entry.getKey(), entry.getValue()));
		}
	}

	private void compressMessage() {
		compressedMessage.clear();
		String encodedChar;
		int nextIndex = 0;
		for (int i = 0; i < charList.size(); i++) {
			encodedChar = this.encodedChar.get(charList.get(i));
			for (int j = 0; j < encodedChar.length(); j++) {
				if (encodedChar.charAt(j) == '0') {
					compressedMessage.set(nextIndex++, false);
				} else {
					compressedMessage.set(nextIndex++, true);
				}
			}
		}
	}

	private Node createHuffTree() {
		createNodeQueue();
		while (prioQueue.size() > 1) {
			Node left = prioQueue.poll();
			Node right = prioQueue.poll();
			prioQueue.add(new Node(null, left.freq + right.freq, left, right));
		}
		return prioQueue.poll();
	}

	private class Node implements Comparable<Node> {
		Character character;
		int freq;
		Node left;
		Node right;

		public void encodeCharacters(String s) {
			if (left == null && right == null) {
				encodedChar.put(character,s);
				return;
			}
			left.encodeCharacters(s + "0");
			right.encodeCharacters(s + "1");
		}

		public Node(Character c, int f, Node l, Node r) {
			this.character = c;
			this.freq = f;
			this.left = l;
			this.right = r;
		}

		public Node(Character c, int f) {
			this(c, f, null, null);
		}

		public Node(int f, Node l, Node r) {
			this(null, f, l, r);
		}

		@Override
		public int compareTo(Node n) {
			return freq - n.freq;
		}

	}

}
