import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PokerService } from '../../services/poker.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="container">
      <h1>🃏 Planning Poker</h1>
      <div class="card">
        <h2>Create a new session</h2>
        <button (click)="create()">Create Session</button>
      </div>
      <div class="card">
        <h2>Join a session</h2>
        <input [(ngModel)]="sessionId" placeholder="Session ID" />
        <input [(ngModel)]="name" placeholder="Your name" />
        <button (click)="join()" [disabled]="!sessionId || !name">Join</button>
      </div>
      @if (error) {
        <p class="error">{{ error }}</p>
      }
    </div>
  `,
  styles: [`
    .container { max-width: 500px; margin: 60px auto; text-align: center; font-family: system-ui; }
    .card { background: #f9f9f9; border-radius: 12px; padding: 24px; margin: 16px 0; }
    input { display: block; width: 100%; padding: 10px; margin: 8px 0; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box; }
    button { background: #1976d2; color: white; border: none; padding: 12px 24px; border-radius: 6px; cursor: pointer; font-size: 1em; margin-top: 8px; }
    button:disabled { background: #ccc; }
    .error { color: red; }
  `]
})
export class HomeComponent {
  sessionId = '';
  name = '';
  error = '';

  constructor(private poker: PokerService, private router: Router) {}

  create() {
    this.poker.createSession().subscribe({
      next: (s) => this.router.navigate(['/session', s.id], { queryParams: { host: true } }),
      error: () => this.error = 'Failed to create session'
    });
  }

  join() {
    this.poker.join(this.sessionId, this.name).subscribe({
      next: () => this.router.navigate(['/session', this.sessionId], { queryParams: { name: this.name } }),
      error: (e) => this.error = e.error?.message || 'Failed to join'
    });
  }
}
