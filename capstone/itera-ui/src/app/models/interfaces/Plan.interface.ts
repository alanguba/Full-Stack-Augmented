export interface Plan {
    id: number;
    nombre: string;
    destino: string;
    dias: number;
    ritmo: string;
    url: string;
    estado: number;
   
}

export interface PlanResponse extends Plan {
 lugaresCount: number;
 createdAt: string;
}