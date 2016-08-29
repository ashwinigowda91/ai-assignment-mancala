import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class test 
{
	public static int player_no;
	public static int depth;
	public static int task;
	public static Map<String,StringBuffer> traverseMap = new HashMap<String,StringBuffer>();
	public static Map<String,String> parentMap = new HashMap<String,String>();
	public static Map<String,Integer> depthMap = new HashMap<String,Integer>();
	public static Map<String,String> valueMap = new HashMap<String,String>();
	public static Map<String,Integer> latestValueMap = new HashMap<String,Integer>();
	public static String outputFname;

	public static void main(String[] args) 
	{
		//read from command line arguments
		String fileName = "C:/Users/ashwini/Downloads/HW2 test cases+grading scripts/HW2 test cases+grading scripts/testInput_21.txt";
		/*String fileName = null;
		if(args.length > 0)
		{
			fileName = args[1];	
		}*/
		List lines = new ArrayList<String>();
		String t;
		String p;
		String d;
		String player1Board;
		String player2Board;
		String p1Mancala;
		String p2Mancala;
		lines = readFile(fileName);
		t = (String) lines.get(0);
		task = Integer.parseInt(t);

		p = (String) lines.get(1);
		player_no = Integer.parseInt(p);

		d = (String)lines.get(2);
		depth = Integer.parseInt(d);
		if(task == 1)
		{
			depth = 1;
		}

		player2Board = (String)lines.get(3);
		int[] player2BoardArr = convertStringToIntArray(player2Board);

		player1Board = (String)lines.get(4);
		int[] player1BoardArr = convertStringToIntArray(player1Board);

		p2Mancala = (String) lines.get(5);
		int player2Mancala = Integer.parseInt(p2Mancala);

		p1Mancala = (String) lines.get(6);
		int player1Mancala = Integer.parseInt(p1Mancala);

		int pitCount = player2BoardArr.length;
		int arraySize = (2*pitCount)+2;
		int[] boardArray = new int[arraySize];

		int j = 0;
		for(int i=0;i<arraySize;i++)
		{
			if(j == arraySize-1)
			{
				boardArray[i] = player2Mancala;
			}
			if(j == (arraySize/2)-1)
			{
				boardArray[i] = player1Mancala;
			}

			if(j<player1BoardArr.length)
			{
				boardArray[i] = player1BoardArr[j];
			}
			j++;
		}

		int k = 0;
		for(int i=(arraySize-2);i>0;i--)
		{	
			if(k < player2BoardArr.length)
			{
				boardArray[i] = player2BoardArr[k];
			}
			k++;
		}

		if(task == 1)
		{
			//calling minimax decision for greedy and minimax algorithm
			greedyMinimaxDecision(player_no, boardArray);
		}

		if(task ==2)
		{
			//calling minimax decision for greedy and minimax algorithm
			outputFname = writeToNewTraverseLog("Node",111111,222222);
			minimaxDecision(player_no, boardArray);
		}

		if(task == 3)
		{
			//calling alpha beta decision
			outputFname = writeToNewTraverseLogAlphaBeta("Node",111111,222222,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
			alphaBetaMinimaxDecision(player_no, boardArray, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}
	}

	public static ArrayList<String> readFile(String fileName)
	{
		List<String> lineElement = new ArrayList<String>();
		try 
		{
			FileInputStream inputfile = null;
			inputfile = new FileInputStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputfile));
			String line = null;

			while((line = reader.readLine()) != null)
			{
				lineElement.add(line);
			}	
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return (ArrayList<String>) lineElement;
	}

	public static Map<String,Integer> generateBoard(int player, int cutOff, int[] initialBoard)
	{
		int countMoves = 0;
		int stoneCount;
		int src;
		int dest;
		int isAtMancala = 0;
		int[] newBoard = new int[initialBoard.length];
		String boardState = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer position = new StringBuffer();
		StringBuffer sbuffer = new StringBuffer();
		String initialBoardState;
		int p1Mancala = 0;
		int p2Mancala = 0;
		String x = null;
		Map<String,Integer> map = new HashMap<String,Integer>();
		Map<String,Integer> utilMap = new HashMap<String, Integer>();

		
		if(player != 1)
		{			
			//initialBoard state represented as a string to keep track of parent

			sbuffer.setLength(0);
			for(int t : initialBoard)
			{
				sbuffer.append(" ").append(t);
			}
			initialBoardState = sbuffer.toString();
			//System.out.println("Parent Board State: "+initialBoardState);

			//looping over initial board state elements
			for(int p=(initialBoard.length-2); p>(initialBoard.length/2)-1; p--)
			{	
				position.setLength(0);
				if(initialBoard[p] != 0)
				{
					isAtMancala = 0;
					for(int f=0;f<initialBoard.length;f++)
					{
						newBoard[f] = initialBoard[f];
					}
					stoneCount = newBoard[p];
					x = Integer.toString(newBoard.length - p);
					newBoard[p] = 0;
					src = p;
					dest = src + 1;
					while(stoneCount > 0)
					{
						if(dest != (newBoard.length))
						{
							p2Mancala = newBoard.length - 1;
							//check if landed in opponent's Mancala
							if(dest == ((newBoard.length/2)-1))
							{
								dest++;
							}
							if(newBoard[dest] == 0 && dest != p2Mancala && ((newBoard.length/2)-1) < dest && dest < (newBoard.length-1) && stoneCount == 1)
							{
								newBoard[p2Mancala] += newBoard[newBoard.length - (dest+2)]+stoneCount;
								newBoard[newBoard.length - (dest+2)] = 0;
								newBoard[dest] = 0;
							}
							else
							{
								newBoard[dest] = newBoard[dest]+1;
							}
							//check if move made is to player's Mancala
							if(dest == p2Mancala && stoneCount == 1)
							{
								isAtMancala = 1;
							}
							dest++;
							stoneCount--;
						}
						else
						{
							dest = 0;
							//check if last stone is put into player's empty pit
							if(newBoard[dest] == 0 && dest != p2Mancala && ((newBoard.length/2)-1) < dest && dest < (newBoard.length-1) && stoneCount == 1)
							{
								newBoard[p2Mancala] += newBoard[newBoard.length - (dest+2)]+stoneCount;
								newBoard[newBoard.length - (dest+2)] = 0;
								newBoard[dest] = 0;
							}
							else
							{
								newBoard[dest] = newBoard[dest]+1;
							}
							
							//check if move made is to player's Mancala
							if(dest == p2Mancala && stoneCount == 1)
							{
								isAtMancala = 1;
							}

							dest++;
							stoneCount--;
						}
					}
					sb.setLength(0);
					for(int t : newBoard)
					{
						sb.append(" ").append(t);
					}
					boardState = sb.toString();
					map.put(boardState, isAtMancala);
					position.append("A").append(x);
					traverseMap.put(boardState,position);
					position = new StringBuffer("");
					if(cutOff == 1)
					{
						parentMap.put(boardState,initialBoardState);
					}
					depthMap.put(boardState, cutOff);

					//update value if depth is reached
					if(cutOff == depth)
					{
						int checkMove = map.get(boardState);
						if(player != player_no)
						{
							if(checkMove == 1)
							{
								valueMap.put(boardState, "Infinity");
							}
							else
							{
								utilMap = computeUtility(player, boardState);
								Integer v = utilMap.get(boardState);
								valueMap.put(boardState, v.toString());
							}
						}	
						else
						{
							if(checkMove == 1)
							{
								valueMap.put(boardState, "-Infinity");
							}
							else
							{
								utilMap = computeUtility(player, boardState);
								Integer v = utilMap.get(boardState);
								valueMap.put(boardState, v.toString());
							}
						}
					}
					//update value if not at depth by checking if self move is made
					else
					{	
						if(player != player_no)
						{
							if(isAtMancala == 1)
							{
								valueMap.put(boardState, "Infinity");
							}
							else
							{
								valueMap.put(boardState, "-Infinity");
							}
						}
						else
						{
							if(isAtMancala == 1)
							{
								valueMap.put(boardState, "-Infinity");
							}
							else
							{
								valueMap.put(boardState, "Infinity");
							}	
						}

					}
				}
			}
		}

		else if(player == 1)
		{	
			//initialBoard state represented as a string to keep track of parent
			sbuffer.setLength(0);
			for(int t : initialBoard)
			{
				sbuffer.append(" ").append(t);
			}
			initialBoardState = sbuffer.toString();

			//looping over the elements of the board state
			for(int p=0; p<(initialBoard.length/2)-1; p++)
			{
				boolean goOverBoard = false;
				position.setLength(0);
				if(initialBoard[p] != 0)
				{
					isAtMancala = 0;
					for(int f=0;f<initialBoard.length;f++)
					{
						newBoard[f] = initialBoard[f];
					}

					stoneCount = initialBoard[p];
					x = Integer.toString(p+2);
					newBoard[p] = 0;
					src = p;
					dest = src + 1;
					while(stoneCount > 0)
					{
						if(dest != (newBoard.length)-1)
						{
							p1Mancala = (newBoard.length/2)-1;
							if(newBoard[dest] == 0 && dest != p1Mancala && (dest < p1Mancala) && stoneCount == 1)
							{								
								newBoard[p1Mancala] += newBoard[initialBoard.length - (dest+2)]+stoneCount;
								newBoard[dest] = 0;
								newBoard[newBoard.length - (dest+2)] = 0;
							}
							else
							{
								newBoard[dest] = newBoard[dest]+1;
							}

							//check if move made is to player's Mancala
							if(dest == p1Mancala && stoneCount == 1)
							{		
								isAtMancala = 1;
							}
							dest++;
							stoneCount--;
						}
						else
						{
							//looping over again from start of board
							dest = 0;
							//check if last stone is put into player's empty pit
							if(newBoard[dest] == 0 && dest != p1Mancala && (dest < p1Mancala) && stoneCount == 1)
							{

								newBoard[p1Mancala] += newBoard[newBoard.length - (dest+2)]+stoneCount;
								newBoard[newBoard.length - (dest+2)] = 0;
								newBoard[dest] = 0;

							}
							else
							{
								newBoard[dest] = newBoard[dest]+1;
							}

							//check if move made is to player's Mancala
							if(dest == p1Mancala && stoneCount == 1)
							{	
								isAtMancala = 1;	
							}
							dest++;
							stoneCount--;
						}
					}

					sb.setLength(0);
					for(int t : newBoard)
					{
						sb.append(" ").append(t);
					}
					boardState = sb.toString();
					map.put(boardState, isAtMancala);
					position.append("B").append(x);
					traverseMap.put(boardState,position);
					position = new StringBuffer("");
					if(cutOff == 1)
					{
						parentMap.put(boardState,initialBoardState);
					}
					depthMap.put(boardState, cutOff);


					//update value map based on if depth is reached
					if(cutOff == depth)
					{
						int checkMove = map.get(boardState);
						if(player != player_no)
						{
							if(checkMove == 1)
							{
								valueMap.put(boardState, "Infinity");
							}
							else
							{
								utilMap = computeUtility(player, boardState);
								Integer v = utilMap.get(boardState);
								valueMap.put(boardState, v.toString());
							}
						}	
						else
						{
							if(checkMove == 1)
							{
								valueMap.put(boardState, "-Infinity");
							}
							else
							{
								utilMap = computeUtility(player, boardState);
								Integer v = utilMap.get(boardState);
								valueMap.put(boardState, v.toString());
							}
						}
					}
					//update value by checking if a self move is made
					else
					{
						if(player != player_no)
						{
							if(isAtMancala == 1)
							{
								valueMap.put(boardState, "Infinity");
							}
							else
							{
								valueMap.put(boardState, "-Infinity");
							}
						}
						else
						{
							if(isAtMancala == 1)
							{
								valueMap.put(boardState, "-Infinity");
							}
							else
							{
								valueMap.put(boardState, "Infinity");
							}	
						}
					}
				}		
			}
		}

		
		return map;
	}

	public static void greedyMinimaxDecision(int player, int[] initialBoard)
	{
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		Map<String, Integer> tMap = new HashMap<String, Integer>();
		Map<String, String> sortMap = new HashMap<String,String>();
		Map<String, Integer> maxMap = new HashMap<String,Integer>();
		Map<String, Integer> minMap = new HashMap<String,Integer>();
		Map<String, Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();

		StringBuffer initParent = new StringBuffer();
		initParent.setLength(0);
		for(int t : initialBoard)
		{
			initParent.append(" ").append(t);
		}
		String initialParent = initParent.toString();
		parentMap.put(initialParent, initialParent);

		//generate valid moves
		tempMap = generateBoard(player, 1, initialBoard);
		int cutOff = 1;

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			//extract values here
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value,val);		
			//call minimax value for the root
			minmaxMap = greedyMinimaxValue(player, value, cutOff, tMap);
			Integer v = null;
			String tempState = null;
			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				v = e.getValue();
				tempState = e.getKey();
			}

			int[] tempArray = convertStringToIntArray(tempState);
			boolean p1temp = false, p2temp = false;
			int p1count = 0;
			for(int p=0;p<(tempArray.length/2)-1;p++)
			{	
				if(tempArray[p] == 0)
				{
					p1count++;
				}
			}

			if(p1count == (tempArray.length/2)-1)
			{
				p1temp = true;
			}

			int p2count = 0;
			for(int p=(tempArray.length-2); p>(tempArray.length/2)-1; p--)
			{	
				if(tempArray[p] == 0)
				{
					p2count++;
				}
			}
			if(p2count == (tempArray.length/2)-1)
			{
				p2temp = true;
			}

			if(p1temp == true || p2temp == true)
			{
				findMaxMap.put(tempState,v);
			}
			else
			{
				findMaxMap.put(value,v);
			}

			//select max of utilities
			maxMap = findMaximum(findMaxMap);
		}

		String parentState = null;
		Integer parentUtil = null;
		String nextState = null;
		for (Map.Entry<String,Integer> parent : maxMap.entrySet())
		{
			parentState = parent.getKey();
			parentUtil = parent.getValue();
		}

		boolean p1tem = false, p2tem = false;			
		int[] tempArr = convertStringToIntArray(parentState);
		int p1cnt = 0;
		for(int p=0;p<(tempArr.length/2)-1;p++)
		{	
			if(tempArr[p] == 0)
			{
				p1cnt++;
			}
		}

		if(p1cnt == (tempArr.length/2)-1)
		{
			p1tem = true;
		}

		int p2cnt = 0;
		for(int p=(tempArr.length-2); p>(tempArr.length/2)-1; p--)
		{	
			if(tempArr[p] == 0)
			{
				p2cnt++;
			}
		}
		if(p2cnt == (tempArr.length/2)-1)
		{
			p2tem = true;
		}

		if(p1tem == true || p2tem == true)
		{
			nextState = parentState;
		}

		else
		{
			if(tempMap.get(parentState) == 1)
			{
				nextState = nextMove(parentState, parentUtil);
			}
			else
			{
				nextState = parentState;
			}
		}
		writeToNextStateFile(nextState);
	}



	public static Map<String,Integer> greedyMinimaxValue(int player, String state, int cutOff, Map<String, Integer> map)
	{
		Map<String,Integer> tMap = new HashMap<String,Integer>();
		Map<String,Integer> tempMap = new HashMap<String, Integer>();
		Map<String,Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();
		Map<String,Integer> utilMap = new HashMap<String,Integer>();
		Map<String,Integer> maxMap = new HashMap<String,Integer>();
		Map<String,String> sortMap = new HashMap<String,String>();
		boolean checkEmpty = false;
		int checkMove;
		int opponentPlayer = 0;
		checkMove = map.get(state);
		boolean checkCutOff = false;
		//utilMap.put("", 0);

		int[] stateArr = convertStringToIntArray(state);

		//check if all pits are empty

		int p1count = 0;
		for(int p=0;p<(stateArr.length/2)-1;p++)
		{	
			if(stateArr[p] == 0)
			{
				p1count++;
			}
		}
		if(p1count == (stateArr.length/2)-1)
		{
			checkEmpty = true;
		}

		int p2count = 0;
		for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
		{	
			if(stateArr[p] == 0)
			{
				p2count++;
			}
		}
		if(p2count == (stateArr.length/2)-1)
		{
			checkEmpty = true;
		}

		if(checkEmpty == true)
		{
			StringBuffer sb = new StringBuffer();
			String newState;
			//collect the stones to opponent's mancala if all pits are empty
			int p1Mancala = stateArr[(stateArr.length/2)-1];
			int p2Mancala = stateArr[stateArr.length-1];
			if(p1count == (stateArr.length/2)-1)
			{
				//looping over player 2's pits
				for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
				{
					p2Mancala += stateArr[p];
					stateArr[stateArr.length-1] = p2Mancala;
					stateArr[p] = 0;
				}
			}
			else
			{
				//looping over player 1's pits
				for(int p=0;p<(stateArr.length/2)-1;p++)
				{
					p1Mancala += stateArr[p];
					stateArr[(stateArr.length/2)-1] = p1Mancala;
					stateArr[p] = 0;
				}
			}
			sb.setLength(0);
			for(int t : stateArr)
			{
				sb.append(" ").append(t);
			}
			newState = sb.toString();
			utilMap = computeUtility(player,newState);
			StringBuffer temp = traverseMap.get(state);
			traverseMap.put(newState,temp);
			if(player == player_no)
			{
				if(cutOff == 1)
				{
					parentMap.put(newState, state);
					latestValueMap.put(newState, utilMap.get(newState));
					latestValueMap.put(state, utilMap.get(newState));
				}
			}
			return utilMap;
		}

		if(cutOff == depth)
		{
			if(checkMove != 1)
			{
				//call function to generate utility value for current state 
				//return the utility and state
				utilMap = computeUtility(player,state);
				StringBuffer s = traverseMap.get(state);
				String str = s.toString();
				int d = depthMap.get(state);
				int v = utilMap.get(state);
				//update latest value for parent
				if(player == player_no)
				{
					if(cutOff == 1)
					{
						latestValueMap.put(state, v);
					}
				}
				return utilMap;
			}
		}

		//check if a self move or not and generate states for player/opponent
		if(checkMove != 1)
		{
			if(cutOff == 1)
			{
				checkCutOff = true;
			}
			cutOff++;
			//flip player because not a self move
			if(player == 1)
			{
				opponentPlayer = 2;
			}
			else if(player == 2)
			{
				opponentPlayer = 1;
			}
			tempMap = generateBoard(opponentPlayer, cutOff, stateArr);
		}
		else
		{
			if(checkMove == 1)
			{
				tempMap = generateBoard(player, cutOff, stateArr);		
			}
		}

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		//call minimax value on the generated states
		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value, val);
			//call minimax recursively
			if(checkMove != 1)
			{
				minmaxMap = greedyMinimaxValue(opponentPlayer, value, cutOff, tMap);	
			}
			else
			{
				minmaxMap = greedyMinimaxValue(player, value, cutOff, tMap);
			}
			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				Integer v = e.getValue();
				findMaxMap.put(value,v);
			}		
			if(player != player_no )
			{
				if(checkMove == 0)
				{
					//select minimum of utilities
					maxMap = findMaximum(findMaxMap);
				}
				else
				{
					//select maximum of utilities
					maxMap = findMinimum(findMaxMap);
				}		
			}

			else
			{
				if(checkMove == 0)
				{
					//select maximum of utilities
					maxMap = findMinimum(findMaxMap);
				}
				else
				{
					//select minimum of utilities
					maxMap = findMaximum(findMaxMap);
				}
			}
		}

		//update latest value for parent
		if(player == player_no)
		{
			if(cutOff == 1 || checkCutOff != false)
			{
				String stat = null;
				for (Map.Entry<String,Integer> e : maxMap.entrySet())
				{
					stat = e.getKey();
				}
				latestValueMap.put(state, maxMap.get(stat));
				Integer childUtil = latestValueMap.get(state);
				maxMap.clear();
				maxMap.put(state, childUtil);
			}
		}
		return maxMap;
	}


	public static void minimaxDecision(int player, int[] initialBoard)
	{
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		Map<String, Integer> tMap = new HashMap<String, Integer>();
		Map<String, String> sortMap = new HashMap<String,String>();
		Map<String, Integer> maxMap = new HashMap<String,Integer>();
		Map<String, Integer> minMap = new HashMap<String,Integer>();
		Map<String, Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();

		//write to file root,zero,-infinity
		writeToExistingTraverseLog(outputFname,"root", 0, 333333);

		StringBuffer initParent = new StringBuffer();
		initParent.setLength(0);
		for(int t : initialBoard)
		{
			initParent.append(" ").append(t);
		}
		String initialParent = initParent.toString();
		parentMap.put(initialParent, initialParent);

		//generate valid moves
		tempMap = generateBoard(player, 1, initialBoard);
		int cutOff = 1;

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			//extract values here
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value,val);		
			//call minimax value for the root
			minmaxMap = minimaxValue(player, value, cutOff, tMap);
			Integer v = null;
			String tempState = null;
			boolean p1temp = false, p2temp = false;

			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				v = e.getValue();
				tempState = e.getKey();	
			}

			int[] tempArray = convertStringToIntArray(tempState);
			int p1count = 0;
			for(int p=0;p<(tempArray.length/2)-1;p++)
			{	
				if(tempArray[p] == 0)
				{
					p1count++;
				}
			}

			if(p1count == (tempArray.length/2)-1)
			{
				p1temp = true;
			}

			int p2count = 0;
			for(int p=(tempArray.length-2); p>(tempArray.length/2)-1; p--)
			{	
				if(tempArray[p] == 0)
				{
					p2count++;
				}
			}
			if(p2count == (tempArray.length/2)-1)
			{
				p2temp = true;
			}

			if(p1temp == true || p2temp == true)
			{
				findMaxMap.put(tempState,v);
			}

			else
			{
				findMaxMap.put(value,v);
			}

			//select max of utilities
			maxMap = findMaximum(findMaxMap);
			String stat;
			Integer b;
			for (Map.Entry<String,Integer> e : maxMap.entrySet())
			{
				stat = e.getKey();
				b = e.getValue();
				writeToExistingTraverseLog(outputFname,"root", 0, b);
			}
		}
		
		String parentState = null;
		Integer parentUtil = null;
		String nextState = null;
		for (Map.Entry<String,Integer> parent : maxMap.entrySet())
		{
			parentState = parent.getKey();
			parentUtil = parent.getValue();
		}

		boolean p1tem = false, p2tem = false;			
		int[] tempArr = convertStringToIntArray(parentState);
		int p1cnt = 0;
		for(int p=0;p<(tempArr.length/2)-1;p++)
		{	
			if(tempArr[p] == 0)
			{
				p1cnt++;
			}
		}

		if(p1cnt == (tempArr.length/2)-1)
		{
			p1tem = true;
		}

		int p2cnt = 0;
		for(int p=(tempArr.length-2); p>(tempArr.length/2)-1; p--)
		{	
			if(tempArr[p] == 0)
			{
				p2cnt++;
			}
		}
		if(p2cnt == (tempArr.length/2)-1)
		{
			p2tem = true;
		}

		if(p1tem == true || p2tem == true)
		{
			nextState = parentState;
		}

		else
		{
			if(tempMap.get(parentState) == 1)
			{
				nextState = nextMove(parentState, parentUtil);
			}
			else
			{
				nextState = parentState;
			}
		}
		writeToNextStateFile(nextState);
	}

	public static String nextMove(String parentState, int parentUtil)
	{
		String childState = null;
		String tempParent = null;
		String nextState = null;
		Map<Integer,String> nextMoveMap = new HashMap<Integer, String>();
		for (Map.Entry<String,String> temp : parentMap.entrySet())
		{
			childState = temp.getKey();
			tempParent = parentMap.get(childState);
			if(tempParent.equals(parentState))
			{	
				if(latestValueMap.get(childState) != null){
					if(latestValueMap.get(childState) == parentUtil)
					{
						StringBuffer nodeName = traverseMap.get(childState);
						String nName = nodeName.substring(1);
						nextMoveMap.put(Integer.parseInt(nName),childState);	
						//parentState = childState;
					}
				}	
			}
		}
		if(!nextMoveMap.isEmpty())
		{
			Object firstKey = nextMoveMap.keySet().toArray()[0];
			nextState = nextMoveMap.get(firstKey);
			return nextMove(nextState, parentUtil);
		}
		return parentState;
	}

	public static Map<String,Integer> minimaxValue(int player, String state, int cutOff, Map<String, Integer> map)
	{
		Map<String,Integer> tMap = new HashMap<String,Integer>();
		Map<String,Integer> tempMap = new HashMap<String, Integer>();
		Map<String,Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();
		Map<String,Integer> utilMap = new HashMap<String,Integer>();
		Map<String,Integer> maxMap = new HashMap<String,Integer>();
		Map<String,String> sortMap = new HashMap<String,String>();
		boolean checkEmpty = false;
		int checkMove;
		int opponentPlayer = 0;
		checkMove = map.get(state);
		boolean checkCutOff = false;
		//utilMap.put("", 0);

		int[] stateArr = convertStringToIntArray(state);
		//check if all pits are empty
		int p1count = 0;
		for(int p=0;p<(stateArr.length/2)-1;p++)
		{	
			if(stateArr[p] == 0)
			{
				p1count++;
			}
		}
		if(p1count == (stateArr.length/2)-1)
		{
			checkEmpty = true;

		}

		int p2count = 0;
		for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
		{	
			if(stateArr[p] == 0)
			{
				p2count++;
			}
		}
		if(p2count == (stateArr.length/2)-1)
		{
			checkEmpty = true;
		}

		if(checkEmpty == true)
		{
			StringBuffer sb = new StringBuffer();
			String newState;
			//collect the stones to opponent's mancala if all pits are empty
			int p1Mancala = stateArr[(stateArr.length/2)-1];
			int p2Mancala = stateArr[stateArr.length-1];
			if(p1count == (stateArr.length/2)-1)
			{
				//looping over player 2's pits
				for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
				{
					p2Mancala += stateArr[p];
					stateArr[stateArr.length-1] = p2Mancala;
					stateArr[p] = 0;
				}
			}
			else
			{
				//looping over player 1's pits
				for(int p=0;p<(stateArr.length/2)-1;p++)
				{
					p1Mancala += stateArr[p];
					stateArr[(stateArr.length/2)-1] = p1Mancala;
					stateArr[p] = 0;
				}
			}
			sb.setLength(0);
			for(int t : stateArr)
			{
				sb.append(" ").append(t);
			}
			newState = sb.toString();
			utilMap = computeUtility(player,newState);
			StringBuffer temp = traverseMap.get(state);
			if(player == player_no)
			{
				if(cutOff == 1)
				{
					traverseMap.put(newState,temp);
					parentMap.put(newState, state);
					latestValueMap.put(newState, utilMap.get(newState));
					latestValueMap.put(state, utilMap.get(newState));
				}
			}
			StringBuffer s = traverseMap.get(state);
			String str = s.toString();
			int d = depthMap.get(state);
			/*if(cutOff != depth || (cutOff == depth && checkMove != 0))
			{
				int val = 0;
				if(valueMap.get(state) == "-Infinity")
				{
					val = 333333;
					writeToExistingTraverseLog(outputFname,str, d, val);
				}
				if(valueMap.get(state) == "Infinity")
				{
					val = 444444;
					writeToExistingTraverseLog(outputFname,str, d, val);
				}
			}*/
			int v = utilMap.get(newState);
			writeToExistingTraverseLog(outputFname,str, d, v);
			return utilMap;
		}

		if(cutOff == depth)
		{
			if(checkMove != 1)
			{
				//call function to generate utility value for current state 
				//return the utility and state
				utilMap = computeUtility(player,state);
				StringBuffer s = traverseMap.get(state);
				String str = s.toString();
				int d = depthMap.get(state);
				int v = utilMap.get(state);
				writeToExistingTraverseLog(outputFname,str, d, v);
				return utilMap;
			}
		}

		//check if a self move or not and generate states for player/opponent
		if(checkMove != 1)
		{
			if(cutOff == 1)
			{
				checkCutOff = true;
			}
			cutOff++;
			//flip player because not a self move
			if(player == 1)
			{
				opponentPlayer = 2;
			}
			else if(player == 2)
			{
				opponentPlayer = 1;
			}
			tempMap = generateBoard(opponentPlayer, cutOff, stateArr);
		}
		else
		{
			if(checkMove == 1)
			{
				tempMap = generateBoard(player, cutOff, stateArr);		
			}
		}

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		StringBuffer s = traverseMap.get(state);
		String str = s.toString();
		int d = depthMap.get(state);
		int v1 = 0;
		if(valueMap.get(state) == "Infinity")
		{
			v1 = 444444;
		}
		else if(valueMap.get(state) == "-Infinity")
		{
			v1 = 333333;
		}
		else
		{
			v1 = Integer.parseInt(valueMap.get(state));
		}

		writeToExistingTraverseLog(outputFname,str, d, v1);


		//call minimax value on the generated states
		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value, val);
			//call minimax recursively
			if(checkMove == 0)
			{
				minmaxMap = minimaxValue(opponentPlayer, value, cutOff, tMap);	
			}
			else
			{
				minmaxMap = minimaxValue(player, value, cutOff, tMap);
			}
			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				Integer v = e.getValue();
				findMaxMap.put(value,v);
			}		
			if(player != player_no )
			{
				if(checkMove == 0)
				{
					//select maximum of utilities
					maxMap = findMaximum(findMaxMap);
					
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					writeToExistingTraverseLog(outputFname,ps, pd, pv);
				}
				else
				{
					//select minimum of utilities
					maxMap = findMinimum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					writeToExistingTraverseLog(outputFname,ps, pd, pv);
				}
			}

			else
			{
				if(checkMove == 0)
				{
					//select minimum of utilities
					maxMap = findMinimum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					writeToExistingTraverseLog(outputFname,ps, pd, pv);
				}
				else
				{
					//select maximum of utilities
					maxMap = findMaximum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					writeToExistingTraverseLog(outputFname,ps, pd, pv);
				}	
			}
		}

		//update latest value for parent
		if(player == player_no)
		{
			if(cutOff == 1 || checkCutOff != false)
			{
				String stat = null;
				for (Map.Entry<String,Integer> e : maxMap.entrySet())
				{
					stat = e.getKey();
				}
				latestValueMap.put(state, maxMap.get(stat));
				Integer childUtil = latestValueMap.get(state);
				maxMap.clear();
				maxMap.put(state, childUtil);
			}
		}
		return maxMap;
	}

	public static int[] convertStringToIntArray(String str)
	{
		String[] trimSpaces = str.trim().split("\\s+");
		int[] intArray = new int[trimSpaces.length];
		for(int i=0;i<trimSpaces.length;i++)
		{
			intArray[i] = Integer.parseInt(trimSpaces[i]);
		}
		return intArray;
	}

	public static Map<String,Integer> computeUtility(int player, String state)
	{
		int[] stateArr = convertStringToIntArray(state);
		Map<String,Integer> utilMap = new HashMap<String,Integer>();
		int arrLen = stateArr.length;
		int util = 0;
		if(player_no != 1)
		{
			util = stateArr[arrLen-1] - stateArr[(arrLen/2)-1];
			//System.out.println("Utility: "+util);
		}

		else
		{
			util = stateArr[(arrLen/2)-1] - stateArr[arrLen-1];
		}
		utilMap.put(state, util);
		return utilMap;
	}

	public static Map<String,Integer> findMaximum(Map<String, Integer> minmaxMap)
	{
		Map<String, Integer> maxMap = new HashMap<String, Integer>();
		String maxState;
		Integer maxValue = 0;
		Iterator<String> itr = minmaxMap.keySet().iterator();
		if((maxState = (String)itr.next()) != "")
		{
			maxValue = minmaxMap.get(maxState);	
			while(itr.hasNext())
			{
				String tempState = (String)itr.next();
				Integer tempValue = minmaxMap.get(tempState);
				if(tempValue == maxValue)
				{
					StringBuffer locationTemp = traverseMap.get(tempState);
					StringBuffer locationMax = traverseMap.get(maxState);
					String locTemp = locationTemp.substring(1);
					String locMax = locationMax.substring(1);
					if(Integer.parseInt(locTemp) < Integer.parseInt(locMax))
					{
						maxValue = tempValue;
						maxState = tempState;
					}
				}

				if(tempValue > maxValue)
				{
					maxValue = tempValue;
					maxState = tempState;				
				}
			}
		}
		maxMap.put(maxState,maxValue);
		return maxMap;
	}

	public static Map<String,Integer> findMinimum(Map<String, Integer> minmaxMap)
	{
		Map<String, Integer> minMap = new HashMap<String, Integer>();
		String minState;
		Integer minValue = 0;
		Iterator<String> itr = minmaxMap.keySet().iterator();
		if((minState = (String)itr.next()) != "")
		{
			minValue = minmaxMap.get(minState);	
			while(itr.hasNext())
			{
				String tempState = (String)itr.next();
				Integer tempValue = minmaxMap.get(tempState);
				if(tempValue == minValue)
				{
					StringBuffer locationTemp = traverseMap.get(tempState);
					StringBuffer locationMax = traverseMap.get(minState);
					String locTemp = locationTemp.substring(1);
					String locMax = locationMax.substring(1);
					if(Integer.parseInt(locTemp) < Integer.parseInt(locMax))
					{
						minValue = tempValue;
						minState = tempState;
					}
				}
				if(tempValue < minValue)
				{
					minValue = tempValue;
					minState = tempState;				
				}
			}
		}
		minMap.put(minState,minValue);
		return minMap;
	}


	public static String writeToNewTraverseLog(String node, int depth, int value)
	{
		String filePath = null;
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		File file = new File("traverse_log.txt");
		try 
		{
			if(file.exists())
			{
				file.delete();
			} 
			file.createNewFile();

			fileWriter = new FileWriter(file.getName(),true);
			bufferWriter = new BufferedWriter(fileWriter);
			filePath = file.getAbsolutePath();
			if(depth == 111111 || value == 222222)
			{
				bufferWriter.append(node+",Depth,"+"Value");
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			bufferWriter.close();
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
		return filePath;
	}

	public static void writeToExistingTraverseLog(String fname, String node, int depth, int value)
	{
		File file = new File(fname);
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;

		try 
		{
			fileWriter = new FileWriter(file.getName(),true);
			bufferWriter = new BufferedWriter(fileWriter);

			if(depth == 111111 || value == 222222)
			{
				bufferWriter.append(node+",Depth,"+"Value");
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else if(value == 333333)
			{
				bufferWriter.append(node+","+depth+","+"-Infinity");
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else if(value == 444444)
			{
				bufferWriter.append(node+","+depth+","+"Infinity");
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else
			{
				bufferWriter.append(node+","+depth+","+value);
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			bufferWriter.close();
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}

	public static void writeToNextStateFile(String nextState) 
	{
		try 
		{
			File newfile = new File("next_state.txt");
			newfile.delete();
			File file = new File("next_state.txt");
			FileWriter fileWriter = new FileWriter(file.getName(),true);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

			int[] stateArr = convertStringToIntArray(nextState);
			for(int p=(stateArr.length-2);p>(stateArr.length/2)-1;p--)
			{
				bufferWriter.append(Integer.toString(stateArr[p]));
				bufferWriter.append(" ");
			}

			bufferWriter.newLine();
			for(int p=0;p<(stateArr.length/2)-1;p++)
			{
				bufferWriter.append(Integer.toString(stateArr[p]));
				bufferWriter.append(" ");
			}

			bufferWriter.newLine();
			bufferWriter.append(Integer.toString(stateArr[stateArr.length-1]));
			bufferWriter.newLine();
			bufferWriter.append(Integer.toString(stateArr[(stateArr.length/2)-1]));
			bufferWriter.close();
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void alphaBetaMinimaxDecision(int player, int[] initialBoard, double alpha, double beta)
	{
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		Map<String, Integer> tMap = new HashMap<String, Integer>();
		Map<String, String> sortMap = new HashMap<String,String>();
		Map<String, Integer> maxMap = new HashMap<String,Integer>();
		Map<String, Integer> minMap = new HashMap<String,Integer>();
		Map<String, Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();

		//write to file root,zero,-infinity,alpha,beta
		writeToExistingTraverseLogAlphaBeta(outputFname,"root", 0, 333333, alpha, beta);

		StringBuffer initParent = new StringBuffer();
		initParent.setLength(0);
		for(int t : initialBoard)
		{
			initParent.append(" ").append(t);
		}
		String initialParent = initParent.toString();
		parentMap.put(initialParent, initialParent);

		tempMap = generateBoard(player, 1, initialBoard);
		int cutOff = 1;

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			//extract values here
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value,val);		
			//call minimax value for the root
			minmaxMap = alphaBetaMinimaxValue(player, value, cutOff, tMap, alpha, beta);
			Integer v = null;
			String tempState = null;
			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				v = e.getValue();
				tempState = e.getKey();
			}

			int[] tempArray = convertStringToIntArray(tempState);
			int p1count = 0;
			boolean p1temp = false, p2temp = false;
			for(int p=0;p<(tempArray.length/2)-1;p++)
			{	
				if(tempArray[p] == 0)
				{
					p1count++;
				}
			}

			if(p1count == (tempArray.length/2)-1)
			{
				p1temp = true;
			}

			int p2count = 0;
			for(int p=(tempArray.length-2); p>(tempArray.length/2)-1; p--)
			{	
				if(tempArray[p] == 0)
				{
					p2count++;
				}
			}
			if(p2count == (tempArray.length/2)-1)
			{
				p2temp = true;
			}

			if(p1temp == true || p2temp == true)
			{
				findMaxMap.put(tempState,v);
			}

			else
			{
				findMaxMap.put(value,v);
			}

			//select maximum of utilities
			maxMap = findMaximum(findMaxMap);
			for (Map.Entry<String,Integer> e : maxMap.entrySet())
			{
				String stat = e.getKey();
				Integer x = e.getValue();
				if(alpha != Double.NEGATIVE_INFINITY)
				{
					if(x > (int)alpha)
					{
						alpha = x;
					}
				}
				else
				{
					alpha = x;
				}
				writeToExistingTraverseLogAlphaBeta(outputFname,"root", 0, x, alpha,beta);
			}
		}

		String parentState = null;
		Integer parentUtil = null;
		String nextState = null;
		for (Map.Entry<String,Integer> parent : maxMap.entrySet())
		{
			parentState = parent.getKey();
			parentUtil = parent.getValue();
		}

		boolean p1tem = false, p2tem = false;			
		int[] tempArr = convertStringToIntArray(parentState);
		int p1cnt = 0;
		for(int p=0;p<(tempArr.length/2)-1;p++)
		{	
			if(tempArr[p] == 0)
			{
				p1cnt++;
			}
		}

		if(p1cnt == (tempArr.length/2)-1)
		{
			p1tem = true;
		}

		int p2cnt = 0;
		for(int p=(tempArr.length-2); p>(tempArr.length/2)-1; p--)
		{	
			if(tempArr[p] == 0)
			{
				p2cnt++;
			}
		}
		if(p2cnt == (tempArr.length/2)-1)
		{
			p2tem = true;
		}

		if(p1tem == true || p2tem == true)
		{
			nextState = parentState;
		}

		else
		{
			if(tempMap.get(parentState) == 1)
			{
				nextState = nextMove(parentState, parentUtil);
			}
			else
			{
				nextState = parentState;
			}
		}
		writeToNextStateFile(nextState);
	}


	public static Map<String,Integer> alphaBetaMinimaxValue(int player, String state, int cutOff, Map<String, Integer> map, double alpha, double beta)
	{
		Map<String,Integer> tMap = new HashMap<String,Integer>();
		Map<String,Integer> tempMap = new HashMap<String, Integer>();
		Map<String,Integer> minmaxMap = new HashMap<String,Integer>();
		Map<String,Integer> findMaxMap = new HashMap<String,Integer>();
		Map<String,Integer> utilMap = new HashMap<String,Integer>();
		Map<String,Integer> maxMap = new HashMap<String,Integer>();
		Map<String,String> sortMap = new HashMap<String,String>();
		boolean checkEmpty = false;
		int checkMove;
		int opponentPlayer = 0;
		checkMove = map.get(state);
		boolean checkCutOff = false;
		String tempState;

		int[] stateArr = convertStringToIntArray(state);
		int p1count = 0;
		for(int p=0;p<(stateArr.length/2)-1;p++)
		{	
			if(stateArr[p] == 0)
			{
				p1count++;
			}
		}
		if(p1count == (stateArr.length/2)-1)
		{
			checkEmpty = true;
		}

		int p2count = 0;
		for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
		{	
			if(stateArr[p] == 0)
			{
				p2count++;
			}
		}
		if(p2count == (stateArr.length/2)-1)
		{
			checkEmpty = true;
		}

		if(checkEmpty == true)
		{
			StringBuffer sb = new StringBuffer();
			String newState;
			//collect the stones to opponent's mancala if all pits are empty
			int p1Mancala = stateArr[(stateArr.length/2)-1];
			int p2Mancala = stateArr[stateArr.length-1];
			if(p1count == (stateArr.length/2)-1)
			{
				//looping over player 2's pits
				for(int p=(stateArr.length-2); p>(stateArr.length/2)-1; p--)
				{
					p2Mancala += stateArr[p];
					stateArr[stateArr.length-1] = p2Mancala;
					stateArr[p] = 0;
				}
			}
			else
			{
				for(int p=0;p<(stateArr.length/2)-1;p++)
				{
					p1Mancala += stateArr[p];
					stateArr[(stateArr.length/2)-1] = p1Mancala;
					stateArr[p] = 0;
				}
			}

			sb.setLength(0);
			for(int t : stateArr)
			{
				sb.append(" ").append(t);
			}
			newState = sb.toString();
			utilMap = computeUtility(player,newState);
			StringBuffer temp = traverseMap.get(state);
			if(player == player_no)
			{
				if(cutOff == 1)
				{
					traverseMap.put(newState,temp);
					parentMap.put(newState, state);
					latestValueMap.put(newState, utilMap.get(newState));
					latestValueMap.put(state, utilMap.get(newState));
				}
			}
			StringBuffer s = traverseMap.get(state);
			String str = s.toString();
			int d = depthMap.get(state);
			/*if(cutOff != depth || (cutOff == depth && checkMove == 1))
			{
				int val = 0;
				if(valueMap.get(state) == "-Infinity")
				{
					val = 333333;
					writeToExistingTraverseLogAlphaBeta(outputFname,str, d, val, alpha, beta);
				}
				if(valueMap.get(state) == "Infinity")
				{
					val = 444444;
					writeToExistingTraverseLogAlphaBeta(outputFname,str, d, val, alpha, beta);
				}
			}*/
			int v = utilMap.get(newState);
			writeToExistingTraverseLogAlphaBeta(outputFname,str, d, v, alpha, beta);
			return utilMap;
		}

		if(cutOff == depth)
		{
			if(checkMove != 1)
			{
				//call function to generate utility value for current state 
				//return the utility and state
				utilMap = computeUtility(player,state);
				StringBuffer s = traverseMap.get(state);
				String str = s.toString();
				int d = depthMap.get(state);
				int v = utilMap.get(state);
				writeToExistingTraverseLogAlphaBeta(outputFname,str, d, v, alpha, beta);
				return utilMap;
			}
		}

		if(checkMove == 0)
		{
			if(cutOff == 1)
			{
				checkCutOff = true;
			}
			cutOff++;
			//flip player because not a self move
			if(player == 2)
			{
				opponentPlayer = 1;
			}
			else if(player == 1)
			{
				opponentPlayer = 2;
			}
			tempMap = generateBoard(opponentPlayer, cutOff, stateArr);
		}
		else
		{
			if(checkMove == 1)
			{
				tempMap = generateBoard(player, cutOff, stateArr);		
			}
		}

		//sort tempMap
		for (Map.Entry<String,Integer> entry : tempMap.entrySet()) 
		{
			String k = entry.getKey();
			StringBuffer loc = traverseMap.get(k);
			String locTemp = loc.substring(1);
			sortMap.put(locTemp, k);
		}

		StringBuffer s = traverseMap.get(state);
		String str = s.toString();
		int d = depthMap.get(state);
		int v1 = 0;
		if(valueMap.get(state) == "Infinity")
		{
			v1 = 444444;
		}
		else if(valueMap.get(state) == "-Infinity")
		{
			v1 = 333333;
		}
		else
		{
			v1 = Integer.parseInt(valueMap.get(state));
		}

		writeToExistingTraverseLogAlphaBeta(outputFname,str, d, v1, alpha, beta);


		//call minimax value on the generated states
		for (Map.Entry<String,String> entry : sortMap.entrySet()) 
		{
			//String s = entry.getKey();
			String value = entry.getValue();
			Integer val = tempMap.get(value);
			tMap.put(value, val);
			//call minimax recursively
			if(checkMove == 1)
			{
				minmaxMap = alphaBetaMinimaxValue(player, value, cutOff, tMap, alpha, beta);
			}
			else
			{
				minmaxMap = alphaBetaMinimaxValue(opponentPlayer, value, cutOff, tMap, alpha, beta);
			}
			for (Map.Entry<String,Integer> e : minmaxMap.entrySet()) 
			{
				Integer v = e.getValue();
				findMaxMap.put(value,v);
			}		
			if(player != player_no )
			{
				if(checkMove == 0)
				{
					//select max of utilities
					maxMap = findMaximum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					double tempAlpha = alpha;
					if(alpha != Double.NEGATIVE_INFINITY)
					{
						if(pv > (int)alpha)
						{
							alpha = pv;
						}
					}

					else
					{
						alpha = pv;
					}

					if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
					{
						if(beta <= alpha)
						{
							pv = (int)alpha;
							tempState = getKeyByValue(maxMap,pv);
							maxMap.clear();
							maxMap.put(tempState, pv);
							writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, tempAlpha, beta);
							return maxMap;
						}
					}
					writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, beta);
				}	
				
				else
				{
					//select min of utilities
					maxMap = findMinimum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					double tempBeta = beta;
					if(beta != Double.POSITIVE_INFINITY)
					{
						if(pv < (int)beta)
						{
							beta = pv;
						}
					}
					else
					{
						beta = pv;
					}

					if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
					{
						if(beta <= alpha)
						{
							pv = (int)beta;
							tempState = getKeyByValue(maxMap,pv);
							maxMap.clear();
							maxMap.put(tempState, pv);
							writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, tempBeta);
							return maxMap;
						}
					}
					writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, beta);
				}
				
			}

			else
			{
				if(checkMove == 0)
				{
					//select minimum of utilities
					maxMap = findMinimum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					double tempBeta = beta;
					if(beta != Double.POSITIVE_INFINITY)
					{
						if(pv < (int)beta)
						{
							beta = pv;
						}
					}
					else
					{
						beta = pv;
					}

					if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
					{
						if(beta <= alpha)
						{
							pv = (int)beta;
							tempState = getKeyByValue(maxMap,pv);
							maxMap.clear();
							maxMap.put(tempState, pv);
							writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, tempBeta);
							return maxMap;
						}
					}
					writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, beta);
				}	
				
				else
				{
					//select maximum of utilities
					maxMap = findMaximum(findMaxMap);
					Integer pv = null;
					StringBuffer pstr = traverseMap.get(state);
					String ps= pstr.toString();
					int pd = depthMap.get(state);
					for (Map.Entry<String,Integer> e : maxMap.entrySet())
					{
						pv = e.getValue();
					}
					double tempAlpha = alpha;
					//update alpha in case of self move and update beta for value
					if(alpha != Double.NEGATIVE_INFINITY)
					{
						if(pv > (int)alpha)
						{
							alpha = pv;
						}
					}
					else
					{
						alpha = pv;
					}

					if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
					{
						if(beta <= alpha)
						{
							pv = (int)alpha;
							tempState = getKeyByValue(maxMap,pv);
							maxMap.clear();
							maxMap.put(tempState, pv);
							writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, tempAlpha, beta);
							return maxMap;
						}
					}
					writeToExistingTraverseLogAlphaBeta(outputFname,ps, pd, pv, alpha, beta);
				}	
			}
		}

		//update latest value for parent
		if(player == player_no)
		{
			if(cutOff == 1 || checkCutOff != false)
			{
				String stat = null;
				for (Map.Entry<String,Integer> e : maxMap.entrySet())
				{
					stat = e.getKey();
				}
				latestValueMap.put(state, maxMap.get(stat));
				Integer childUtil = latestValueMap.get(state);
				maxMap.clear();
				maxMap.put(state, childUtil);
			}
		}
		return maxMap;
	}

	public static String writeToNewTraverseLogAlphaBeta(String node, int depth, int value, Double alpha, Double beta)
	{
		String filePath = null;

		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;
		File file = new File("traverse_log.txt");
		try 
		{
			if(file.exists())
			{
				file.delete();
			} 
			file.createNewFile();

			fileWriter = new FileWriter(file.getName(),true);
			bufferWriter = new BufferedWriter(fileWriter);
			filePath = file.getAbsolutePath();
			if(depth == 111111 || value == 222222)
			{
				bufferWriter.append(node+",Depth"+",Value"+",Alpha"+",Beta");
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			bufferWriter.close();
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
		return filePath;
	}

	public static void writeToExistingTraverseLogAlphaBeta(String fname, String node, int depth, int value, double alpha, double beta)
	{
		File file = new File(fname);
		FileWriter fileWriter = null;
		BufferedWriter bufferWriter = null;

		try 
		{
			fileWriter = new FileWriter(file.getName(),true);
			bufferWriter = new BufferedWriter(fileWriter);

			if(depth == 111111 || value == 222222)
			{
				bufferWriter.append(node+",Depth"+",Value"+","+alpha+","+beta);
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else if(value == 333333)
			{
				if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+"-Infinity"+","+(int)alpha+","+(int)beta);
				}
				else if(alpha == Double.NEGATIVE_INFINITY && beta == Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+"-Infinity"+","+Double.NEGATIVE_INFINITY+","+Double.POSITIVE_INFINITY);
				}
				else if(alpha == Double.NEGATIVE_INFINITY || beta == Double.POSITIVE_INFINITY)
				{
					if(alpha == Double.NEGATIVE_INFINITY)
					{
						bufferWriter.append(node+","+depth+","+"-Infinity"+","+Double.NEGATIVE_INFINITY+","+(int)beta);
					}
					else
					{
						bufferWriter.append(node+","+depth+","+"-Infinity"+","+(int)alpha+","+Double.POSITIVE_INFINITY);
					}
				}
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else if(value == 444444)
			{
				if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+"Infinity"+","+(int)alpha+","+(int)beta);
				}
				else if(alpha == Double.NEGATIVE_INFINITY && beta == Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+"Infinity"+","+Double.NEGATIVE_INFINITY+","+Double.POSITIVE_INFINITY);
				}
				else if(alpha == Double.NEGATIVE_INFINITY || beta == Double.POSITIVE_INFINITY)
				{
					if(alpha == Double.NEGATIVE_INFINITY)
					{
						bufferWriter.append(node+","+depth+","+"Infinity"+","+Double.NEGATIVE_INFINITY+","+(int)beta);
					}
					else
					{
						bufferWriter.append(node+","+depth+","+"Infinity"+","+(int)alpha+","+Double.POSITIVE_INFINITY);
					}
				}
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			else
			{
				if(alpha != Double.NEGATIVE_INFINITY && beta != Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+value+","+(int)alpha+","+(int)beta);
				}
				else if(alpha == Double.NEGATIVE_INFINITY && beta == Double.POSITIVE_INFINITY)
				{
					bufferWriter.append(node+","+depth+","+value+","+Double.NEGATIVE_INFINITY+","+Double.POSITIVE_INFINITY);
				}
				else if(alpha == Double.NEGATIVE_INFINITY || beta == Double.POSITIVE_INFINITY)
				{
					if(alpha == Double.NEGATIVE_INFINITY)
					{
						bufferWriter.append(node+","+depth+","+value+","+Double.NEGATIVE_INFINITY+","+(int)beta);
					}
					else
					{
						bufferWriter.append(node+","+depth+","+value+","+(int)alpha+","+Double.POSITIVE_INFINITY);
					}
				}
				bufferWriter.flush();
				bufferWriter.newLine();
			}
			bufferWriter.close();
			fileWriter.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}	
	}

	public static String getKeyByValue(Map<String,Integer> maxMap, int util)
	{
		String key = null;
		for (Map.Entry<String,Integer> entry : maxMap.entrySet()) 
		{
			if (entry.getValue().equals(util)) 
			{
				key = entry.getKey();
			}
		}
		return key;
	}
}
