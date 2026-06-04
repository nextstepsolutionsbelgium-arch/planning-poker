import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PokerService, SessionResponse } from '../../services/poker.service';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container" *ngIf="session">
      <h2>🃏 Session</h2>
      <p class="session-id">ID: <code>{{ session.id }}</code>
        <button class="small" (click)="copyLink()">📋 Copy link</button>
      </p>

      <!-- Join panel if not yet joined -->
      <div *ngIf="!name" class="card">
        <input [(ngModel)]="nameInput" placeholder="Your name" />
        <button (click)="joinSession()">Join</button>
      </div>

      <!-- Ticket -->
      <div *ngIf="isHost" class="card">
        <input [(ngModel)]="ticketId" placeholder="JIRA ticket (e.g. PROJ-123)" />
        <button (click)="resetVotes()">New Round</button>
      </div>
      <p *ngIf="session.ticketId" class="ticket">🎫 {{ session.ticketId }}</p>

      <!-- Voting cards -->
      <div *ngIf="name && !session.revealed" class="cards">
        <button *ngFor="let p of points" class="point-card"
                [class.selected]="selectedVote === p"
                (click)="submitVote(p)">{{ p }}</button>
      </div>

      <!-- Participants -->
      <div class="participants">
        <h3>Participants</h3>
        <div *ngFor="let p of session.participants" class="participant">
          <span class="name">{{ p.name }}</span>
          <span class="vote" [class.hidden]="!session.revealed && !p.hasVoted">
            {{ session.revealed ? p.vote : (p.hasVoted ? '✓' : '…') }}
          </span>
        </div>
      </div>

      <!-- Reveal button -->
      <button *ngIf="isHost && !session.revealed" class="reveal-btn" (click)="revealVotes()">
        👁 Reveal Votes
      </button>

      <!-- Results -->
      <div *ngIf="session.revealed" class="results">
        <h3>Results</h3>
        <div *ngFor="let p of session.participants" class="result-row">
          <strong>{{ p.name }}</strong>: {{ p.vote || '—' }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 600px; margin: 40px auto; font-family: system-ui; text-align: center; }
    .session-id { font-size: 0.85em; color: #666; }
    .session-id code { background: #eee; padding: 2px 8px; border-radius: 4px; }
    .card { background: #f9f9f9; border-radius: 12px; padding: 20px; margin: 12px 0; }
    input { padding: 10px; border: 1px solid #ddd; border-radius: 6px; margin-right: 8px; }
    button { background: #1976d2; color: white; border: none; padding: 10px 20px; border-radius: 6px; cursor: pointer; }
    button.small { padding: 4px 10px; font-size: 0.8em; background: #666; }
    .cards { display: flex; flex-wrap: wrap; justify-content: center; gap: 10px; margin: 20px 0; }
    .point-card { width: 60px; height: 80px; font-size: 1.4em; background: #fff; border: 2px solid #1976d2; color: #1976d2; border-radius: 10px; }
    .point-card.selected { background: #1976d2; color: white; }
    .participants { text-align: left; margin: 20px 0; }
    .participant { display: flex; justify-content: space-between; padding: 8px 12px; border-bottom: 1px solid #eee; }
    .reveal-btn { background: #e65100; padding: 14px 28px; font-size: 1.1em; margin: 16px 0; }
    .results { text-align: left; margin: 20px 0; }
    .result-row { padding: 6px 0; }
    .ticket { font-size: 1.2em; font-weight: bold; }
  `]
})
export class SessionComponent implements OnInit, OnDestroy {
  session: SessionResponse | null = null;
  name = '';
  nameInput = '';
  isHost = false;
  ticketId = '';
  selectedVote = '';
  points = ['1', '2', '3', '5', '8', '13', '21', '?'];
  private pollInterval: any;

  constructor(private route: ActivatedRoute, private poker: PokerService) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.isHost = this.route.snapshot.queryParamMap.get('host') === 'true';
    this.name = this.route.snapshot.queryParamMap.get('name') || '';
    this.loadSession(id);
    this.pollInterval = setInterval(() => this.loadSession(id), 2000);
  }

  ngOnDestroy() {
    clearInterval(this.pollInterval);
  }

  loadSession(id: string) {
    this.poker.getSession(id).subscribe(s => this.session = s);
  }

  joinSession() {
    if (!this.nameInput || !this.session) return;
    this.poker.join(this.session.id, this.nameInput).subscribe(() => {
      this.name = this.nameInput;
    });
  }

  submitVote(vote: string) {
    if (!this.session) return;
    this.selectedVote = vote;
    this.poker.vote(this.session.id, this.name, vote).subscribe();
  }

  revealVotes() {
    if (!this.session) return;
    this.poker.reveal(this.session.id).subscribe(s => this.session = s);
  }

  resetVotes() {
    if (!this.session) return;
    this.poker.reset(this.session.id, this.ticketId).subscribe(s => {
      this.session = s;
      this.selectedVote = '';
    });
  }

  copyLink() {
    navigator.clipboard.writeText(window.location.href);
  }
}
