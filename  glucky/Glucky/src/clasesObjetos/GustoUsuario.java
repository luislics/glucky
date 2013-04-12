package clasesObjetos;

import java.util.ArrayList;

public class GustoUsuario {
	public int nivelGusto;
	public int idComida;
	public String fechaAgregado;
	public GustoUsuario(int idComida, int nivelGusto, String fechaAgregado){
		this.nivelGusto = nivelGusto;
		this.idComida = idComida;
		this.fechaAgregado = fechaAgregado;
	}
	public static int getNivel(int idComida, ArrayList<GustoUsuario> gustos){
		for (GustoUsuario gusto : gustos) {
			if(gusto.idComida == idComida){
				return gusto.nivelGusto;
			}
		}
		return 0;
	}
	public static void setNivel(int idComida, ArrayList<GustoUsuario> gustos, int nivel,String fecha){
		for(GustoUsuario gusto : gustos){
			if(gusto.idComida == idComida){
				gusto.nivelGusto = nivel;
				return;
			}
		}
		gustos.add(new GustoUsuario(idComida,nivel,fecha));
	}
}
