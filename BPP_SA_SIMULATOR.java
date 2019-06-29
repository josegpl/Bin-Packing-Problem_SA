import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BPP_SA_SIMULATOR {
	
	private static int capacity;
	private static int no_items;
	private static Item items[];
	private static int bin_capacity[];
	private static int iter;
	private static double temp=1000.0;
	private static double temp_tar=0.1;
	private static double cooling_factor=0.95;
	private static int current_fx;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Random ran = new Random();
		
		
		System.out.println();
		
		System.out.print("Inserir capacidade dos pacotes : ");
		capacity = sc.nextInt();
		sc.nextLine();
		System.out.println();
		
		getInstances();
		
		
		//Solução aleatória inicial
		FirstFitDecreasing ffd = new FirstFitDecreasing();
		bin_capacity = ffd.getMinimumBins(items , capacity);
		
		for(int i=0;i<bin_capacity.length;i++){
			System.out.print(bin_capacity[i] + "  ");
		}
		
		System.out.println();
		System.out.print("Numero de iteracoes: (>=100): ");
		iter = sc.nextInt();
		
		current_fx = F();
		while(iter>0){
			iter--;
			temp=1000.0;
			double threshold = Math.log(0.00001)/Math.log(cooling_factor);
			threshold = 1000*Math.pow(cooling_factor,Math.ceil(threshold/2));
//			System.out.println(threshold);
			while(temp > temp_tar){
				
				if(temp>threshold){
					int rand = ran.nextInt(100);
					if(rand<=70){
						swap10();
					}else{
						swap11();
					}
				}else{
					int rand = ran.nextInt(100);
					if(rand<=70){
						swap11();
					}else{
						swap10();
					}
				}
				temp = cooling_factor*temp;
				for(int id=0;id<bin_capacity.length;id++){
					System.out.print(bin_capacity[id] + "  ");
				}
				System.out.println();
			}
		}
		for(int i=0;i<bin_capacity.length;i++){
			System.out.print(bin_capacity[i] + "  "); //capacidade final restante de cada pacote
		}
		sc.close();
		//Mostra a forma como cada item foi armazenado em cada pacote
		System.out.println();
		System.out.print("Tamanho it: "); //tamanho dos itens
		for(int i=0; i<items.length; i++) {
			System.out.print(items[i].size + " ");
			}
		System.out.println();
		System.out.print("Num pacote: ");
		for(int i=0; i<items.length; i++) { //armazenado em qual pacote
		System.out.print(items[i].bin_no + " ");
		}
		System.out.println();
		int res = 0;
		for(int i=0; i<items.length; i++) {
			if(items[i].bin_no > res) {
				res = items[i].bin_no;
			}
		}
		res = res+1;
		System.out.println("\nSolução: " + res); //solução final encontrada em relação ao número de pacotes utilizados
	}
	
	private static void swap11(){
		Random ran=new Random();
		int i,j;
		i=ran.nextInt(no_items);
		j=ran.nextInt(no_items);
		if(i!=j && items[i].size != items[j].size && bin_capacity[items[i].bin_no]+items[i].size>=items[j].size && bin_capacity[items[j].bin_no]+items[j].size>=items[i].size ){
			bin_capacity[items[i].bin_no]+=(items[i].size-items[j].size);
			bin_capacity[items[j].bin_no]+=(items[j].size-items[i].size);
			int bno=items[i].bin_no;
			items[i].bin_no=items[j].bin_no;
			items[j].bin_no=bno;
			int next_fx=F();
			if(next_fx>current_fx){
				current_fx=next_fx;
			}else{
				double exp=Math.exp((double)(next_fx-current_fx)/temp);
				exp=1.0-exp;
				double prob=ran.nextDouble();
				if(prob < exp){
					current_fx = next_fx;
				}else{
					bin_capacity[items[i].bin_no]+=(items[i].size-items[j].size);
					bin_capacity[items[j].bin_no]+=(items[j].size-items[i].size);
					int bn0=items[i].bin_no;
					items[i].bin_no=items[j].bin_no;
					items[j].bin_no=bn0;
				}
			}
		}
	}
	
	private static void swap10(){
		System.out.print("swap10 => ");
		Random ran = new Random();
		int ri = ran.nextInt(no_items);
		int bno = -1;
		int max_fx = Integer.MIN_VALUE;
		for(int i=0;i<bin_capacity.length;i++){
			if(i!=items[ri].bin_no && bin_capacity[i] >= items[ri].size){
				bin_capacity[items[ri].bin_no]+=(items[ri].size);
				bin_capacity[i]-=(items[ri].size);
				int temp_bno=items[ri].bin_no;
				items[ri].bin_no=i;
				int next_fx = F();
				if(next_fx > max_fx){
					bno = i;
					max_fx = next_fx;
				}
				items[ri].bin_no=temp_bno;
				bin_capacity[items[ri].bin_no]-=(items[ri].size);
				bin_capacity[i]+=(items[ri].size);
			}
		}
		
		
		if(max_fx>current_fx){
			current_fx=max_fx;
			if(bno!=-1){
				System.out.print("ch : ");
				bin_capacity[items[ri].bin_no]+=(items[ri].size);
				bin_capacity[bno]-=(items[ri].size);
				items[ri].bin_no=bno;
			}
		}
		else if(max_fx != Integer.MIN_VALUE){
			double exp=Math.exp((double)(max_fx-current_fx)/temp);
			exp=1.0-exp;
			double prob=ran.nextDouble();
			if(prob < exp){
				current_fx = max_fx;
				if(bno!=-1){
					bin_capacity[items[ri].bin_no]+=(items[ri].size);
					bin_capacity[bno]-=(items[ri].size);
					items[ri].bin_no=bno;
				}
			}
		}
		for(int i=0;i<bin_capacity.length;i++){
			if(bin_capacity[i]==capacity){
				int temp_capaity[] = new int[bin_capacity.length-1];
				int index=0;
				for(int j=0;j<bin_capacity.length;j++){
					if(j!=i){
						temp_capaity[index] = bin_capacity[j];
						index++;
					}
				}
				bin_capacity=temp_capaity;
				for(int j=0;j<no_items;j++){
					if(items[j].bin_no>i){
						items[j].bin_no--;
					}
				}
			}
		}
		
	}
	
	private static int F(){
		int ans=0;
		
		HashMap<Integer, Integer> hm = new HashMap<>();
		for(int i=0;i<no_items;i++){
			if(hm.containsKey(items[i].bin_no)){
				hm.put(items[i].bin_no, hm.get(items[i].bin_no)+items[i].size);
			}else{
				hm.put(items[i].bin_no, items[i].size);
			}
		}
		
		Set entrySet = hm.entrySet();
		Iterator it = entrySet.iterator();
		
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			ans += ((int)me.getValue() * (int)me.getValue());
		}
		
		return ans;
	}
	
	
	private static void getInstances() {
		Scanner sc = new Scanner(System.in);
		System.out.println("==> Instance Generator");
		
		System.out.print("No. de itens : ");
		no_items = sc.nextInt();
		items = new Item[no_items];
	
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("inst2.txt"));
			String aux;
			int size;
			
			int cont = 0;
			while((aux = br.readLine()) != null ) {
				
				System.out.print(aux + " ");
				size = Integer.parseInt(aux);
				System.out.println(size);
		
				items[cont] = new Item(size);
				cont++;	
			
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
