import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface ParticipantView {
  name: string;
  vote: string | null;
  hasVoted: boolean;
}

export interface SessionResponse {
  id: string;
  ticketId: string | null;
  revealed: boolean;
  participants: ParticipantView[];
}

@Injectable({ providedIn: 'root' })
export class PokerService {
  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createSession(): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${this.api}/sessions`, {});
  }

  getSession(id: string): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(`${this.api}/sessions/${id}`);
  }

  join(sessionId: string, name: string): Observable<any> {
    return this.http.post(`${this.api}/sessions/${sessionId}/join`, { name });
  }

  vote(sessionId: string, name: string, vote: string): Observable<any> {
    return this.http.post(`${this.api}/sessions/${sessionId}/vote`, { name, vote });
  }

  reveal(sessionId: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${this.api}/sessions/${sessionId}/reveal`, {});
  }

  reset(sessionId: string, ticketId?: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${this.api}/sessions/${sessionId}/reset`, { ticketId });
  }
}
